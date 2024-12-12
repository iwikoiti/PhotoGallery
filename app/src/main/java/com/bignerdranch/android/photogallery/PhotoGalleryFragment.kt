package com.bignerdranch.android.photogallery

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

private const val TAG = "PhotoGalleryFragment"
class PhotoGalleryFragment: Fragment() {

    private lateinit var photoGalleryViewModel: PhotoGalleryViewModel

    private lateinit var photoRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        photoGalleryViewModel = ViewModelProvider(this)[PhotoGalleryViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_photo_gallery, container, false)

        photoRecyclerView = view.findViewById(R.id.photo_recycler_view)
        photoRecyclerView.layoutManager = GridLayoutManager(context, 3)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photoGalleryViewModel.galleryItemLiveData.observe(
            viewLifecycleOwner,
            Observer{ galleryItems->
                photoRecyclerView.adapter = PhotoAdapter(galleryItems)
            }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater){
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_photo_gallery, menu)

        val searchItem: MenuItem = menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as SearchView

        searchView.apply {
            setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(queryText: String): Boolean {
                    Log.d(TAG, "QueryTextSubmit: $queryText")
                    photoGalleryViewModel.fetchPhotos(queryText)
                    return true
                }

                override fun onQueryTextChange(queryText: String): Boolean {
                    Log.d(TAG,  "QueryTextChange: $queryText")
                    return false
                }
            })

            setOnSearchClickListener{
                searchView.setQuery(photoGalleryViewModel.searchTerm, false)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_item_clear -> {
                photoGalleryViewModel.fetchPhotos("")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private class PhotoHolder(private val itemImageView: ImageView): RecyclerView.ViewHolder(itemImageView){
        val bindDrawable: (Drawable)->Unit = itemImageView::setImageDrawable

        fun bindGalleryItem(galleryItem: GalleryItem){
            Picasso.get().load(galleryItem.url).placeholder(R.drawable.bill_up_close).into(itemImageView)
        }
    }

    private inner class PhotoAdapter(private val galleryItems: List<GalleryItem>):
        RecyclerView.Adapter<PhotoHolder>() {

            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): PhotoHolder {
                val view = layoutInflater.inflate(
                    R.layout.list_item_gallery,
                    parent,
                    false
                ) as ImageView
                return PhotoHolder(view)
            }

            override fun getItemCount(): Int = galleryItems.size

            override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
                val galleryItem = galleryItems[position]
                holder.bindGalleryItem(galleryItem)
            }
        }

    companion object {
        fun newInstance() = PhotoGalleryFragment()
    }
}