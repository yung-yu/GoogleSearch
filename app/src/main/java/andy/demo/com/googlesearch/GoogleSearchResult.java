package andy.demo.com.googlesearch;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by andy on 2015/3/21.
 */
public class GoogleSearchResult {


    int responseStatus;
    String responseDetails;
    ResponseData  responseData;


    /**
     *  -2 input Parms error
     *  -1 IOexception
     *  0 no json content
     *
     */
    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseDetails() {
        return responseDetails;
    }

    public ResponseData getResponseData() {
        return responseData;
    }

    public class ResponseData {
        List<Results> results;
        Cursor cursor ;

        public List<Results> getResults() {
            return results;
        }

        public Cursor getCursor() {
            return cursor;
        }

        public class Results{
           String GsearchResultClass;
           String width;
           String height;
           String imageId;
           String tbWidth;
           String tbHeight;
           String unescapedUrl;
           String url;
           String visibleUrl;
           String title;
           String titleNoFormatting;
           String originalContextUrl;
           String content;
           String contentNoFormatting;
           String tbUrl;

            public String getGsearchResultClass() {
                return GsearchResultClass;
            }

            public String getWidth() {
                return width;
            }

            public String getHeight() {
                return height;
            }

            public String getImageId() {
                return imageId;
            }

            public String getTbWidth() {
                return tbWidth;
            }

            public String getTbHeight() {
                return tbHeight;
            }

            public String getUnescapedUrl() {
                return unescapedUrl;
            }

            public String getUrl() {
                return url;
            }

            public String getVisibleUrl() {
                return visibleUrl;
            }

            public String getTitle() {
                return title;
            }

            public String getTitleNoFormatting() {
                return titleNoFormatting;
            }

            public String getOriginalContextUrl() {
                return originalContextUrl;
            }

            public String getContent() {
                return content;
            }

            public String getContentNoFormatting() {
                return contentNoFormatting;
            }

            public String getTbUrl() {
                return tbUrl;
            }
        }

        public class Cursor{
             String resultCount;

             List<Pages> pages;
              public class Pages{
                String start;
                Integer label;

                public String getStart() {
                    return start;
                }

                public Integer getLabel() {
                    return label;
                }
            }
             String estimatedResultCount;
             Integer currentPageIndex;
             String moreResultsUrl;
             String searchResultTime;

            public String getResultCount() {
                return resultCount;
            }

            public List<Pages> getPagers() {
                return pages;
            }

            public String getEstimatedResultCount() {
                return estimatedResultCount;
            }

            public Integer getCurrentPageIndex() {
                return currentPageIndex;
            }

            public String getMoreResultsUrl() {
                return moreResultsUrl;
            }

            public String getSearchResultTime() {
                return searchResultTime;
            }



        }
    }


}
