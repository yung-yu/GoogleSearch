
#聲明
---
###本作品只提供研究和研討之用途使用,不提供任何營利用途之使用.
<br/>

##實現
---
###利用GSON來取得Google SearchImage api回傳JSON解析和以RecyclerView實現瀑布流呈現結果。
[測試影片](https://youtu.be/PBiXWbLiLt4)


<br>

##使用
---
###利用 git clone ,再使用Android Studio用open an existing Android Studio project開啓專案。
<br/>

##說明
---
###Google搜尋圖片API
```
https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=搜尋關鍵字&start=開始數字&rsz=8
```
###回傳結果為JSON,則使用GSON來解析。
<br>
###下一步,使用RecyclerView需要先設定一個LayoutManager。
```
StaggeredGridLayoutManager mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(瀑布流攔位數, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mStaggeredGridLayoutManager);            
```        
###然後再RecyclerView.Adapterb更新View項目，再重新更新View寬高。
```
//根據攔位數更新寬度
int width = recyclerView.getWidth() / rowCount;
//利用Ｂitmap寬高比算出高度
int height = width * bitmap.getHeight() / bitmap.getWidth();
RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.cardView.getLayoutParams();
params.width = width;
params.height = height;
viewHolder.cardView.setLayoutParams(params);
viewHolder.iv.setImageBitmap(bitmap);
```

<br>
##參考
---
[Google Search Image 如何找圖片? Tickr內輕鬆做到的方式](http://www.takobear.tw/201702608526356260322804024687/-google-search-image-tickr)



