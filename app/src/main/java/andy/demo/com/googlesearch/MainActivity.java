package andy.demo.com.googlesearch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;


public class MainActivity extends ActionBarActivity {
    RecyclerView recyclerView;
    LinearLayoutManager mLayoutManager;
    RecycleAdapter recycleAdapter;
    GoogleSearchResult googleSearchResult;
    Toolbar toolbar;
    ImageLoader imageLoader;
    SwipeRefreshLayout refreshLayout;
    boolean isLoadingMore = false;
    StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    boolean isEnd = false;
    int rowCount = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        setSupportActionBar(toolbar);

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.drawable.onlytrue)
                .delayBeforeLoading(100)
                .displayer(new RoundedBitmapDisplayer(5)).build();



        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .defaultDisplayImageOptions(options)
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(rowCount, StaggeredGridLayoutManager.VERTICAL);//表示两列，并且是竖直方向的瀑布流
        recyclerView.setLayoutManager(mStaggeredGridLayoutManager);


        // specify an adapter (see also next example)
        recycleAdapter = new RecycleAdapter(this);
        recyclerView.setAdapter(recycleAdapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isLoadingMore)
                    refreshLayout.setRefreshing(false);
            }
        });
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int[] lastVisibleItemArray = mStaggeredGridLayoutManager.findLastVisibleItemPositions(null);
                int  lastVisibleItem = 0;
                for (int i :lastVisibleItemArray){
                    lastVisibleItem = Math.max(lastVisibleItem,i);
                }
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                Log.d("googleSearch", "lastVisibleItem :" + lastVisibleItem);
                Log.d("googleSearch", "dy :" + dy);

                Log.d("googleSearch", "totalItemCount :" + totalItemCount);
                boolean  isNotFull = recyclerView.getHeight() >= mStaggeredGridLayoutManager.getHeight();
                //lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - rowCount&& (dy > 0||isNotFull)) {
                    if(isLoadingMore){
                        //Log.d(TAG,"ignore manually update!");
                    } else{
                        if(googleSearchResult!=null){
                            int curIndex = googleSearchResult.getResponseData().getCursor().getCurrentPageIndex();
                            List<GoogleSearchResult.ResponseData.Cursor.Pages> pagers=googleSearchResult.getResponseData().getCursor().getPagers();
                            curIndex++;
                            if(pagers!=null&&curIndex<pagers.size()){
                                isLoadingMore = true;
                                refreshLayout.setRefreshing(true);
                                new GoogleSearch(MainActivity.this,searchCallBack).execute(curKey,
                                        googleSearchResult.getResponseData().getCursor().getPagers().get(curIndex).getStart());
                                return;
                            }
                        }else{
                            isLoadingMore = false;
                        }
                        refreshLayout.setRefreshing(false);
                    }
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
          MenuItem searchItem = menu.findItem(R.id.action_search);
          final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("請輸入查詢關鍵字");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                queryKeyWorld(s);
                searchView.setQuery("",false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        imageLoader.resume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        imageLoader.pause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        imageLoader.stop();
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        imageLoader.destroy();
        super.onDestroy();

    }

    String curKey = "";
    GoogleSearch googleSearch;
    public void queryKeyWorld(String keyworld){
        keyworld = keyworld.replace(" ","_");
        if(!keyworld.isEmpty()&&!curKey.equals(keyworld)) {
            curKey = keyworld;
            recycleAdapter.reSet();
            Log.d("googleSearch", "keyworld :" + keyworld);
            new GoogleSearch(MainActivity.this,searchCallBack).execute(keyworld, "0");
        }

    }
    private GoogleSearchCallBack searchCallBack = new  GoogleSearchCallBack(){
        @Override
        public void onResult(GoogleSearchResult result) {
          if(result.getResponseStatus()==200){
              googleSearchResult = result;
              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      recycleAdapter.setData(googleSearchResult.getResponseData().getResults());
                      refreshLayout.setRefreshing(false);
                      isLoadingMore = false;
                  }
              });

          }else{
              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      refreshLayout.setRefreshing(false);
                      isLoadingMore = false;
                  }
              });
          }
        }
    };

    private class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder>{
        public  class ViewHolder extends RecyclerView.ViewHolder {
            ImageView iv;
            CardView cardView;
            public ViewHolder(View view) {
                super(view);
                iv = (ImageView) view.findViewById(R.id.imageView);
                cardView = (CardView) view.findViewById(R.id.card_view);
            }
        }
        Context context;

        List<GoogleSearchResult.ResponseData.Results> data;

        public void setData(List<GoogleSearchResult.ResponseData.Results> data) {
            if(this.data==null) {
                this.data = data;
                notifyDataSetChanged();
            }
            else {
                int start = data.size();
                this.data.addAll(data);
                int end = data.size();
                for(;start<end;start++) {
                    notifyItemInserted(start);
                }
            }

        }
        public void reSet(){
            this.data = null;
            isEnd = false;
            notifyDataSetChanged();
        }
        public RecycleAdapter(Context context) {
            super();
            this.context = context;


        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.imageitem,null);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int i) {
            GoogleSearchResult.ResponseData.Results item = getItem(i);

            String url = item.getTbUrl();
            viewHolder.iv.setTag(url);
            imageLoader.displayImage(url,viewHolder.iv,new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                    viewHolder.cardView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    viewHolder.iv.setImageResource(R.drawable.onlytrue);
                }

                @Override
                public void onLoadingComplete(String imgUrl, View view, Bitmap bitmap) {
                    if (viewHolder.iv.getTag() != null
                            && viewHolder.iv.getTag().equals(imgUrl)){
                        int width = recyclerView.getWidth() / rowCount;
                        int height = width * bitmap.getHeight() / bitmap.getWidth();
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.cardView.getLayoutParams();

                        params.width = width;
                        params.height = height;

                        viewHolder.cardView.setLayoutParams(params);
                        viewHolder.iv.setImageBitmap(bitmap);
                        viewHolder.cardView.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,android.R.anim.fade_in));
                        viewHolder.cardView.setVisibility(View.VISIBLE);


                    }
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public void setHasStableIds(boolean hasStableIds) {
            super.setHasStableIds(hasStableIds);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }
        public GoogleSearchResult.ResponseData.Results getItem(int position){
            if(data!=null)
                if( position<data.size())
                    return data.get(position);
            return null;
        }
        @Override
        public int getItemCount() {
            if(data!=null)
                return data.size();
            return 0;
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            super.onViewRecycled(holder);
//            if(holder.iv.getDrawable() instanceof BitmapDrawable){
//                BitmapDrawable bd = (BitmapDrawable) holder.iv.getDrawable();
//                if(bd.getBitmap()!=null){
//                    if(!bd.getBitmap().isRecycled())
//                        bd.getBitmap().recycle();
//                }
//            }

        }

        @Override
        public boolean onFailedToRecycleView(ViewHolder holder) {
            return super.onFailedToRecycleView(holder);
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
        }

        @Override
        public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
            super.registerAdapterDataObserver(observer);
        }

        @Override
        public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
            super.unregisterAdapterDataObserver(observer);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
        }
    }
}
