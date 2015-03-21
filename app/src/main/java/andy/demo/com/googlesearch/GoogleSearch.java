package andy.demo.com.googlesearch;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by andy on 2015/3/21.
 */
public class GoogleSearch extends AsyncTask<String ,Integer,GoogleSearchResult> {

    private final String IMAGE_URL ="https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=%k&start=%i&rsz=8";
    private Context context;
    private  GoogleSearchCallBack callBack;
    public GoogleSearch(Context context ,GoogleSearchCallBack callBack ){
      this.context = context;
      this.callBack = callBack;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(GoogleSearchResult googleSearchResult) {
        super.onPostExecute(googleSearchResult);
        if(googleSearchResult!=null){
            Log.d("googleSearch", "status code :" + googleSearchResult.getResponseStatus());
            if(callBack!=null)
                callBack.onResult(googleSearchResult);
        }



    }




    @Override
    protected void onCancelled() {
        super.onCancelled();

    }

    /**
     *
     * @param params
     * @return
     */
    @Override
    protected GoogleSearchResult doInBackground(String... params) {
        GoogleSearchResult result = new GoogleSearchResult();
        Log.d("googleSearch", "params :" + params);
        if(params.length!=2) {
            result.setResponseStatus(-2);
            return result;
        }
        String url = IMAGE_URL.replace("%k",params[0]);
               url = url.replace("%i",params[1]);
        Log.d("googleSearch", "search api :" + url);
        HttpClient client  = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse response = client.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == 200){

                String jsonStr = getInputStreamToString(response.getEntity().getContent());
                Log.d("googleSearch", jsonStr);
                if(!jsonStr.isEmpty()) {
                    Gson gson = new Gson();
                    GoogleSearchResult googleSearchResult = gson.fromJson(jsonStr, GoogleSearchResult.class);

                    return googleSearchResult;
                }else{
                    result.setResponseStatus(0);
                    return result;
                }
            }
            else{
                result.setResponseStatus(statusCode);
            }
            return result;
        }catch (IOException e){
            e.printStackTrace();
            result.setResponseStatus(-1);
            return result;
        }
    }

    public String getInputStreamToString(InputStream is){
        BufferedReader br = null;
         StringBuffer sb = new StringBuffer();
        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();


    }
}
