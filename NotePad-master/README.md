# 安卓基于NotePad的功能拓展
## 实现功能
* NoteList中显示条目增加时间戳显示
* 添加笔记查询功能（根据标题查询）
## 源码分析
**实现显示条目增加时间戳需要完成以下内容**
* 更改布局文件,添加一个TextView用于显示时间戳    
  此处顺带为每个笔记添加一个标签图片
```
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="100dp"
    android:orientation="horizontal">
    <ImageView
        android:layout_width="69dp"
        android:layout_height="88dp"
        android:background="@drawable/app_notes" />
    <LinearLayout  xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">
    <TextView xmlns:android="http://schemas.android.com/apk/res/android"
        android:id="@android:id/text1"
        android:layout_width="match_parent"
        android:layout_height="?android:attr/listPreferredItemHeight"
        android:textAppearance="?android:attr/textAppearanceLarge"
        android:gravity="center_vertical"
        android:paddingLeft="5dip"
        android:singleLine="true"
        />
    <TextView
        android:id="@+id/date"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textAppearance="?android:attr/textAppearanceSmall"
        android:paddingLeft="5dip"
        />
</LinearLayout>
</LinearLayout>
```
实现后的效果:
* 更改布局文件之后,查看源代码发现数据库中已经存在了实现时间戳所需要的字段
```
public void onCreate(SQLiteDatabase db) {
           db.execSQL("CREATE TABLE " + NotePad.Notes.TABLE_NAME + " ("
                   + NotePad.Notes._ID + " INTEGER PRIMARY KEY,"
                   + NotePad.Notes.COLUMN_NAME_TITLE + " TEXT,"
                   + NotePad.Notes.COLUMN_NAME_NOTE + " TEXT,"
                   + NotePad.Notes.COLUMN_NAME_CREATE_DATE + " INTEGER,"
                   + NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE + " INTEGER"
                   + ");");
       }
```
再分析noteList,发现使用了Cursor类来取出数据,PROJECTION为需要取出的列
```
Cursor cursor = managedQuery(
            getIntent().getData(),            // Use the default content URI for the provider.
            PROJECTION,                       // Return the note ID and title for each note.
            null,                             // No where clause, return all records.
            null,                             // No where clause, therefore no where column values.
            NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
        );
```
因此我们在PROJECTION中增加一个数据库中的修改时间字段  
```
private static final String[] PROJECTION = new String[] {
            NotePad.Notes._ID, // 0
            NotePad.Notes.COLUMN_NAME_TITLE, //1
            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE,
    };
```  
继续分析可以看到,程序通过SimpleCursorApadpter来实现Cursor与布局中控件的绑定  
```  
 // Creates the backing adapter for the ListView.
        SimpleCursorAdapter adapter
            = new SimpleCursorAdapter(
                      this,                             // The Context for the ListView
                      R.layout.noteslist_item,          // Points to the XML for a list item
                      cursor,                           // The cursor to get items from
                      dataColumns,
                      viewIDs
              );
```  
因此我们将之前在布局文件中为显示时间戳写入的新的TextView以及我们新投影出的修改时间字段添加进datacolunmns与ViewIds中
```  
 // The names of the cursor columns to display in the view, initialized to the title column
        String[] dataColumns = { NotePad.Notes.COLUMN_NAME_TITLE,NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE} ;
        // The view IDs that will display the cursor columns, initialized to the TextView in
        // noteslist_item.xml
        int[] viewIDs = { android.R.id.text1,R.id.date};
```   
* 此时时间戳已经可以正常的实现,但是存在问题,因为数据库读出的数据是与1970年的差值转化完的毫秒数,我们要将其转化为正常的时间  
在NoteEditor类中找到修改笔记时更新数据库的代码,将修改时间在存储进数据库之前做处理
```  
 Long now = Long.valueOf(System.currentTimeMillis());
        Date date = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        String dateTime = format.format(date);
        values.put(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, dateTime);
```  
同样的,在NoteProvider类中找到笔记创建时,插入笔记数据的代码,添加以下处理,并将存入时间的数据更改为处理过的dataTime
```  
Long now = Long.valueOf(System.currentTimeMillis());
        Date date = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        String dateTime = format.format(date);
```  
至此,拓展功能一就完成了
**添加笔记查询功能（根据标题查询）**
* 首先,添加笔记查询功能需要在list_option_menu中增加一个搜索的按钮,这里使用安卓自带的搜索图标
```  
<item
        android:id="@+id/menu_search"
        android:icon="@android:drawable/ic_search_category_default"
        android:title="Search"
        android:showAsAction="always"
        />
```  
效果如下:  
此时仅仅只有图标,还不能使用因此我们要实现搜索的功能
* 首先实现一个Search的布局文件,当点击搜索按钮以后,用该布局文件显示,这里使用了SearchView以及ListView
```  
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="match_parent">
    <SearchView
        android:id="@+id/search_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:iconifiedByDefault="false"
        android:queryHint="输入搜索内容..."
        android:layout_alignParentTop="true">
    </SearchView>
    <ListView
        android:id="@android:id/list"
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
    </ListView>
</LinearLayout>
```  
实现后效果:  
* 同样我们要实现搜索栏点击的触发需要再NoteList类中对对应的点击进行修改,触发intent,显式调用一个Search的activity
```  
 @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_add:
          /*
           * Launches a new Activity using an Intent. The intent filter for the Activity
           * has to have action ACTION_INSERT. No category is set, so DEFAULT is assumed.
           * In effect, this starts the NoteEditor Activity in NotePad.
           */
           startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
           return true;
        case R.id.menu_paste:
          /*
           * Launches a new Activity using an Intent. The intent filter for the Activity
           * has to have action ACTION_PASTE. No category is set, so DEFAULT is assumed.
           * In effect, this starts the NoteEditor Activity in NotePad.
           */
          startActivity(new Intent(Intent.ACTION_PASTE, getIntent().getData()));
          return true;
        case R.id.menu_search:
           Intent intent = new Intent();
           intent.setClass(NotesList.this,NoteSearch.class);
           NotesList.this.startActivity(intent);
           return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
```  
* 因此我们还需要编写一个search类,用于与之前编写好的search布局文件一起实现搜索的功能,search类需要继承ListView,并实现  
  SearchView以及OnQueryTextListener接口,实现的思路与NoteList一样,创建一个PROJECTION的数组,表示取出的列名  
  通过Cursor取出数据,然后通过SimpleCursor实现数据绑定,唯一的区别在于,需要增加一个搜索栏的监听以及实现模糊查询
``` 
public class NoteSearch extends ListActivity  implements SearchView.OnQueryTextListener {
    private static final String[] PROJECTION = new String[] {
            NotePad.Notes._ID, 
            NotePad.Notes.COLUMN_NAME_TITLE, 
            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, 
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        Intent intent = getIntent();
        if (intent.getData() == null) {
            intent.setData(NotePad.Notes.CONTENT_URI);
        }
        SearchView searchview = (SearchView)findViewById(R.id.search_view);
        //为查询文本框注册监听器
        searchview.setOnQueryTextListener(NoteSearch.this);
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        String selection = NotePad.Notes.COLUMN_NAME_TITLE + " Like ? ";  //sql模糊查询语句
        String[] selectionArgs = { "%"+newText+"%" };   //添加模糊查询参数
        //Cursor游标查询符合条件数据
        Cursor cursor = managedQuery(
                getIntent().getData(),            // Use the default content URI for the provider.
                PROJECTION,                       // Return the note ID and title for each note. and modifcation date
                selection,
                selectionArgs,
                NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
        );
        //与noteList一样,设置读取的列内容,这里是标题与时间
        String[] dataColumns = { NotePad.Notes.COLUMN_NAME_TITLE ,  NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE };
        int[] viewIDs = { android.R.id.text1 , R.id.date}; //对应的视图显示位置与数据绑定
        //调用适配器绑定数据
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.noteslist_item,
                cursor,
                dataColumns,
                viewIDs
        );
        setListAdapter(adapter);
        return true;
    }
    //监听器
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // Constructs a new URI from the incoming URI and the row ID
        Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
        // Gets the action from the incoming Intent
        String action = getIntent().getAction();
        // Handles requests for note data
        if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action)) {
            // Sets the result to return to the component that called this Activity. The
            // result contains the new URI
            setResult(RESULT_OK, new Intent().setData(uri));
        } else {
            // Sends out an Intent to start an Activity that can handle ACTION_EDIT. The
            // Intent's data is the note ID URI. The effect is to call NoteEdit.
            startActivity(new Intent(Intent.ACTION_EDIT, uri));
        }
    }
}
``` 
* 最后不要遗漏Intent使用时,在manifest添加activity以及filter,此处返回多列数据因此mimeType为vnd.android.cursor.dir
``` 
 <activity
       android:name=".NoteSearch"
       android:label="NoteSearch">
            <intent-filter>
                <action android:name="android.intent.action.NoteSearch" />
                <action android:name="android.intent.action.SEARCH" />
                <action android:name="android.intent.action.SEARCH_LONG_PRESS" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:mimeType="vnd.android.cursor.dir/vnd.google.note" />
            </intent-filter>
        </activity>
```
这样就实现了搜索功能
效果如下:
