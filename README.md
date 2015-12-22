# android-fan-menu
A custom ViewGroup in form of a fan menu. Reminds of the iOS Path menu.

## Usage
Add a dependency to your `build.gradle`:
```
dependencies {
  compile 'me.hammarstrom.fanmenu:fanmenu:1.0';
}
```

Add the `me.hammarstrom.fanmenu.FanMenu` to your XML layout file:
```XML
    <me.hammarstrom.fanmenu.FanMenu
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentRight="true"
        android:layout_alignParentBottom="true"
        app:menu_position="right">

        <!--
        // Will act as the menu button
        -->
        <android.support.design.widget.FloatingActionButton
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginRight="20dp"
            android:layout_marginBottom="20dp"
            android:layout_marginLeft="20dp"
            android:src="@drawable/ic_add_white_24dp"/>

        <!--
        // The following views will act as menu items
        -->
        <android.support.design.widget.FloatingActionButton
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:src="@drawable/ic_donut_small_white_24dp"
            android:visibility="gone"
            app:backgroundTint="@color/itemOne"
            app:fabSize="mini" />

        <android.support.design.widget.FloatingActionButton
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:src="@drawable/ic_donut_small_white_24dp"
            android:visibility="gone"
            app:backgroundTint="@color/itemTwo"
            app:fabSize="mini"/>

        <android.support.design.widget.FloatingActionButton
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:src="@drawable/ic_donut_small_white_24dp"
            android:visibility="gone"
            app:backgroundTint="@color/itemThree"
            app:fabSize="mini"/>

        <android.support.design.widget.FloatingActionButton
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:src="@drawable/ic_donut_small_white_24dp"
            android:visibility="gone"
            app:backgroundTint="@color/itemFour"
            app:fabSize="mini"/>

        <android.support.design.widget.FloatingActionButton
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:src="@drawable/ic_donut_small_white_24dp"
            android:visibility="gone"
            app:backgroundTint="@color/itemFive"
            app:fabSize="mini"/>

    </me.hammarstrom.fanmenu.FanMenu>
```
The first child view (preferably `android.support.design.widget.FloatingActionButton`) will act as the menu button, and the rest of them will act as menu items.

## License
```
Copyright 2015 Fredrik Hammarström

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```