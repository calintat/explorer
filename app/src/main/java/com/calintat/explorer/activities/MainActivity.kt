package com.calintat.explorer.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.customtabs.CustomTabsIntent
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast

import com.calintat.explorer.R
import com.calintat.explorer.recycler.Adapter
import com.calintat.explorer.recycler.OnItemSelectedListener
import com.calintat.explorer.ui.InputDialog
import com.calintat.explorer.utils.FileUtils

import java.io.File
import java.util.ArrayList
import java.util.Locale

import com.github.calintat.getInt
import com.github.calintat.putInt
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {

        private val SAVED_DIRECTORY = "com.calintat.explorer.SAVED_DIRECTORY"

        private val SAVED_SELECTION = "com.calintat.explorer.SAVED_SELECTION"

        private val EXTRA_NAME = "com.calintat.explorer.EXTRA_NAME"

        private val EXTRA_TYPE = "com.calintat.explorer.EXTRA_TYPE"
    }

    private var currentDirectory: File? = null

    private var adapter: Adapter? = null

    private var name: String? = null

    private var type: String? = null

    //----------------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {

        initActivityFromIntent()

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        initAppBarLayout()

        initDrawerLayout()

        initFloatingActionButton()

        initNavigationView()

        initRecyclerView()

        loadIntoRecyclerView()

        invalidateToolbar()

        invalidateTitle()
    }

    override fun onBackPressed() {

        if (drawerLayout!!.isDrawerOpen(navigationView!!)) {

            drawerLayout!!.closeDrawers()

            return
        }

        if (adapter!!.anySelected()) {

            adapter!!.clearSelection()

            return
        }

        if (!FileUtils.isStorage(currentDirectory)) {

            setPath(currentDirectory!!.parentFile)

            return
        }

        super.onBackPressed()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == 0) {

            if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                Snackbar.make(coordinatorLayout, "Permission required", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Settings") { v -> gotoApplicationSettings() }
                        .show()
            } else {

                loadIntoRecyclerView()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onResume() {

        if (adapter != null) adapter!!.refresh()

        super.onResume()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {

        adapter!!.select(savedInstanceState.getIntegerArrayList(SAVED_SELECTION)!!)

        val path = savedInstanceState.getString(SAVED_DIRECTORY, FileUtils.internalStorage.path)

        if (currentDirectory != null) setPath(File(path))

        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putIntegerArrayList(SAVED_SELECTION, adapter!!.selectedPositions)

        outState.putString(SAVED_DIRECTORY, FileUtils.getPath(currentDirectory))

        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.action, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.action_delete -> {
                actionDelete()
                return true
            }

            R.id.action_rename -> {
                actionRename()
                return true
            }

            R.id.action_search -> {
                actionSearch()
                return true
            }

            R.id.action_copy -> {
                actionCopy()
                return true
            }

            R.id.action_move -> {
                actionMove()
                return true
            }

            R.id.action_send -> {
                actionSend()
                return true
            }

            R.id.action_sort -> {
                actionSort()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {

        if (adapter != null) {

            val count = adapter!!.selectedItemCount

            menu.findItem(R.id.action_delete).isVisible = count >= 1

            menu.findItem(R.id.action_rename).isVisible = count >= 1

            menu.findItem(R.id.action_search).isVisible = count == 0

            menu.findItem(R.id.action_copy).isVisible = count >= 1 && name == null && type == null

            menu.findItem(R.id.action_move).isVisible = count >= 1 && name == null && type == null

            menu.findItem(R.id.action_send).isVisible = count >= 1

            menu.findItem(R.id.action_sort).isVisible = count == 0
        }

        return super.onPrepareOptionsMenu(menu)
    }

    //----------------------------------------------------------------------------------------------

    private fun initActivityFromIntent() {

        name = intent.getStringExtra(EXTRA_NAME)

        type = intent.getStringExtra(EXTRA_TYPE)

        if (type != null) {

            when (type) {

                "audio" -> setTheme(R.style.app_theme_Audio)

                "image" -> setTheme(R.style.app_theme_Image)

                "video" -> setTheme(R.style.app_theme_Video)
            }
        }
    }

    private fun loadIntoRecyclerView() {

        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE

        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, permission)) {

            ActivityCompat.requestPermissions(this, arrayOf(permission), 0)

            return
        }

        val context = this

        if (name != null) {

            adapter!!.addAll(FileUtils.searchFilesName(context, name!!))

            return
        }

        if (type != null) {

            when (type) {

                "audio" -> adapter!!.addAll(FileUtils.getAudioLibrary(context))

                "image" -> adapter!!.addAll(FileUtils.getImageLibrary(context))

                "video" -> adapter!!.addAll(FileUtils.getVideoLibrary(context))
            }

            return
        }

        setPath(FileUtils.internalStorage)
    }

    //----------------------------------------------------------------------------------------------

    private fun initAppBarLayout() {

        toolbar.overflowIcon = ContextCompat.getDrawable(this, R.drawable.ic_more)

        setSupportActionBar(toolbar)
    }

    private fun initDrawerLayout() {

        if (name != null || type != null) {

            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    private fun initFloatingActionButton() {

        fab.setOnClickListener { v -> actionCreate() }

        if (name != null || type != null) {

            val layoutParams = fab.layoutParams

            (layoutParams as CoordinatorLayout.LayoutParams).anchorId = View.NO_ID

            fab.layoutParams = layoutParams

            fab.hide()
        }
    }

    private fun initNavigationView() {

        val menuItem = navigationView.menu.findItem(R.id.navigation_external)

        menuItem.isVisible = FileUtils.externalStorage != null

        navigationView!!.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_audio -> {
                    setType("audio")
                    return@setNavigationItemSelectedListener true
                }

                R.id.navigation_image -> {
                    setType("image")
                    return@setNavigationItemSelectedListener true
                }

                R.id.navigation_video -> {
                    setType("video")
                    return@setNavigationItemSelectedListener true
                }

                R.id.navigation_feedback -> {
                    gotoFeedback()
                    return@setNavigationItemSelectedListener true
                }

                R.id.navigation_settings -> {
                    gotoSettings()
                    return@setNavigationItemSelectedListener true
                }
            }

            drawerLayout!!.closeDrawers()

            when (item.itemId) {

                R.id.navigation_directory_0 -> {
                    setPath(FileUtils.getPublicDirectory("DCIM"))
                    return@setNavigationItemSelectedListener true
                }

                R.id.navigation_directory_1 -> {
                    setPath(FileUtils.getPublicDirectory("Download"))
                    return@setNavigationItemSelectedListener true
                }

                R.id.navigation_directory_2 -> {
                    setPath(FileUtils.getPublicDirectory("Movies"))
                    return@setNavigationItemSelectedListener true
                }

                R.id.navigation_directory_3 -> {
                    setPath(FileUtils.getPublicDirectory("Music"))
                    return@setNavigationItemSelectedListener true
                }

                R.id.navigation_directory_4 -> {
                    setPath(FileUtils.getPublicDirectory("Pictures"))
                    return@setNavigationItemSelectedListener true
                }

                else -> return@setNavigationItemSelectedListener true
            }
        }

        val textView = navigationView!!.getHeaderView(0).findViewById(R.id.header) as TextView

        textView.text = FileUtils.getStorageUsage(this)

        textView.setOnClickListener { startActivity(Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS)) }
    }

    private fun initRecyclerView() {

        adapter = Adapter(this)

        adapter!!.setOnItemClickListener(OnItemClickListener(this))

        adapter!!.setOnItemSelectedListener(object : OnItemSelectedListener {

            override fun onItemSelected() {

                invalidateOptionsMenu()

                invalidateTitle()

                invalidateToolbar()
            }
        })

        if (type != null) {

            when (type) {

                "audio" -> {
                    adapter!!.setItemLayout(R.layout.list_item_1)
                    adapter!!.setSpanCount(resources.getInteger(R.integer.span_count1))
                }

                "image" -> {
                    adapter!!.setItemLayout(R.layout.list_item_2)
                    adapter!!.setSpanCount(resources.getInteger(R.integer.span_count2))
                }

                "video" -> {
                    adapter!!.setItemLayout(R.layout.list_item_3)
                    adapter!!.setSpanCount(resources.getInteger(R.integer.span_count3))
                }
            }
        } else {

            adapter!!.setItemLayout(R.layout.list_item_0)

            adapter!!.setSpanCount(resources.getInteger(R.integer.span_count0))
        }

        if (recyclerView != null) recyclerView.adapter = adapter
    }

    //----------------------------------------------------------------------------------------------

    private fun invalidateTitle() {

        if (adapter!!.anySelected()) {

            val selectedItemCount = adapter!!.selectedItemCount

            toolbarLayout!!.title = String.format("%s selected", selectedItemCount)
        } else if (name != null) {

            toolbarLayout!!.title = String.format("Search for %s", name)
        } else if (type != null) {

            when (type) {

                "image" -> toolbarLayout!!.title = "Images"

                "audio" -> toolbarLayout!!.title = "Music"

                "video" -> toolbarLayout!!.title = "Videos"
            }
        } else if (currentDirectory != null && currentDirectory != FileUtils.internalStorage) {

            toolbarLayout!!.title = FileUtils.getName(currentDirectory!!)
        } else {

            toolbarLayout!!.title = resources.getString(R.string.app_name)
        }
    }

    private fun invalidateToolbar() {

        if (adapter!!.anySelected()) {

            toolbar!!.setNavigationIcon(R.drawable.ic_clear)

            toolbar!!.setNavigationOnClickListener { v -> adapter!!.clearSelection() }
        } else if (name == null && type == null) {

            toolbar!!.setNavigationIcon(R.drawable.ic_menu)

            toolbar!!.setNavigationOnClickListener { v -> drawerLayout!!.openDrawer(navigationView) }
        } else {

            toolbar!!.setNavigationIcon(R.drawable.ic_back)

            toolbar!!.setNavigationOnClickListener { v -> finish() }
        }
    }

    //----------------------------------------------------------------------------------------------

    private fun actionCreate() {

        val inputDialog = object : InputDialog(this, "Create", "Create directory") {

            override fun onActionClick(text: String) {

                try {
                    val directory = FileUtils.createDirectory(currentDirectory!!, text)

                    adapter!!.clearSelection()

                    adapter!!.add(directory)
                } catch (e: Exception) {

                    showMessage(e)
                }

            }
        }

        inputDialog.show()
    }

    private fun actionDelete() {

        actionDelete(adapter!!.getSelectedItems())

        adapter!!.clearSelection()
    }

    private fun actionDelete(files: List<File>) {

        val sourceDirectory = currentDirectory

        adapter!!.removeAll(files)

        val message = String.format("%s files deleted", files.size)

        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setAction("Undo") { v ->

                    if (currentDirectory == null || currentDirectory == sourceDirectory) {

                        adapter!!.addAll(files)
                    }
                }
                .addCallback(object : Snackbar.Callback() {

                    override fun onDismissed(snackbar: Snackbar?, event: Int) {

                        if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {

                            try {

                                for (file in files) FileUtils.deleteFile(file)
                            } catch (e: Exception) {

                                showMessage(e)
                            }

                        }

                        super.onDismissed(snackbar, event)
                    }
                })
                .show()
    }

    private fun actionRename() {

        val selectedItems = adapter!!.getSelectedItems()

        val inputDialog = object : InputDialog(this, "Rename", "Rename") {

            override fun onActionClick(text: String) {

                adapter!!.clearSelection()

                try {

                    if (selectedItems.size == 1) {

                        val file = selectedItems[0]

                        val index = adapter!!.indexOf(file)

                        adapter!!.updateItemAt(index, FileUtils.renameFile(file, text))
                    } else {

                        val size = selectedItems.size.toString().length

                        val format = " (%0" + size + "d)"

                        for (i in selectedItems.indices) {

                            val file = selectedItems[i]

                            val index = adapter!!.indexOf(file)

                            val newFile = FileUtils.renameFile(file, text + String.format(format, i + 1))

                            adapter!!.updateItemAt(index, newFile)
                        }
                    }
                } catch (e: Exception) {

                    showMessage(e)
                }

            }
        }

        if (selectedItems.size == 1) {

            inputDialog.setDefault(FileUtils.removeExtension(selectedItems[0].name))
        }

        inputDialog.show()
    }

    private fun actionSearch() {

        val inputDialog = object : InputDialog(this, "Search", "Search") {

            override fun onActionClick(text: String) {

                setName(text)
            }
        }

        inputDialog.show()
    }

    private fun actionCopy() {

        val selectedItems = adapter!!.getSelectedItems()

        adapter!!.clearSelection()

        transferFiles(selectedItems, false)
    }

    private fun actionMove() {

        val selectedItems = adapter!!.getSelectedItems()

        adapter!!.clearSelection()

        transferFiles(selectedItems, true)
    }

    private fun actionSend() {

        val intent = Intent(Intent.ACTION_SEND_MULTIPLE)

        intent.type = "*/*"

        val uris = ArrayList<Uri>()

        for (file in adapter!!.getSelectedItems()) {

            if (file.isFile) uris.add(Uri.fromFile(file))
        }

        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)

        startActivity(intent)
    }

    private fun actionSort() {

        val builder = AlertDialog.Builder(this)

        val checkedItem = this.getInt("pref_sort", 0)

        val sorting = arrayOf("Name", "Last modified", "Size (high to low)")

        val context = this

        builder.setSingleChoiceItems(sorting, checkedItem) { dialog, which ->

            adapter!!.update(which)

            context.putInt("pref_sort", which)

            dialog.dismiss()
        }

        builder.setTitle("Sort by")

        builder.show()
    }

    //----------------------------------------------------------------------------------------------

    private fun transferFiles(files: List<File>, delete: Boolean) {

        val paste = if (delete) "moved" else "copied"

        val message = String.format(Locale.getDefault(), "%d items waiting to be %s", files.size, paste)

        val onClickListener = { view: View ->

            try {

                for (file in files) {

                    adapter!!.addAll(FileUtils.copyFile(file, currentDirectory!!))

                    if (delete) FileUtils.deleteFile(file)
                }
            } catch (e: Exception) {

                showMessage(e)
            }
        }

        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE)
                .setAction("Paste", onClickListener)
                .show()
    }

    private fun showMessage(e: Exception) {

        showMessage(e.message!!)
    }

    private fun showMessage(message: String) {

        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    //----------------------------------------------------------------------------------------------

    private fun gotoFeedback() {

        val builder = CustomTabsIntent.Builder()

        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary0))

        builder.build().launchUrl(this, Uri.parse("https://github.com/calintat/Explorer/issues"))
    }

    private fun gotoSettings() {

        startActivity(Intent(this, SettingsActivity::class.java))
    }

    private fun gotoApplicationSettings() {

        val intent = Intent()

        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS

        intent.data = Uri.fromParts("package", "com.calintat.explorer", null)

        startActivity(intent)
    }

    private fun setPath(directory: File) {

        if (!directory.exists()) {

            Toast.makeText(this, "Directory doesn't exist", Toast.LENGTH_SHORT).show()

            return
        }

        currentDirectory = directory

        adapter!!.clear()

        adapter!!.clearSelection()

        adapter!!.addAll(*FileUtils.getChildren(directory)!!)

        invalidateTitle()
    }

    private fun setName(name: String) {

        val intent = Intent(this, MainActivity::class.java)

        intent.putExtra(EXTRA_NAME, name)

        startActivity(intent)
    }

    private fun setType(type: String) {

        val intent = Intent(this, MainActivity::class.java)

        intent.putExtra(EXTRA_TYPE, type)

        if (Build.VERSION.SDK_INT >= 21) {

            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
        }

        startActivity(intent)
    }

    //----------------------------------------------------------------------------------------------

    private inner class OnItemClickListener (private val context: Context) : com.calintat.explorer.recycler.OnItemClickListener {

        override fun onItemClick(position: Int) {

            val file = adapter!![position]

            if (adapter!!.anySelected()) {

                adapter!!.toggle(position)

                return
            }

            if (file.isDirectory) {

                if (file.canRead()) {

                    setPath(file)
                } else {

                    showMessage("Cannot open directory")
                }
            } else {

                if (Intent.ACTION_GET_CONTENT == intent.action) {

                    val intent = Intent()

                    intent.setDataAndType(Uri.fromFile(file), FileUtils.getMimeType(file))

                    setResult(Activity.RESULT_OK, intent)

                    finish()
//                } else if (FileUtils.FileType.getFileType(file) == FileUtils.FileType.ZIP) {
//
//                    val dialog = ProgressDialog.show(context, "", "Unzipping", true)
//
//                    val thread = Thread {
//
//                        try {
//
//                            setPath(FileUtils.unzip(file))
//
//                            runOnUiThread { dialog.dismiss() }
//                        } catch (e: Exception) {
//
//                            showMessage(e)
//                        }
//                    }
//
//                    thread.run()
                } else {

                    try {

                        val intent = Intent(Intent.ACTION_VIEW)

                        intent.setDataAndType(Uri.fromFile(file), FileUtils.getMimeType(file))

                        startActivity(intent)
                    } catch (e: Exception) {

                        showMessage(String.format("Cannot open %s", FileUtils.getName(file)))
                    }

                }
            }
        }

        override fun onItemLongClick(position: Int): Boolean {

            adapter!!.toggle(position)

            return true
        }
    }
}