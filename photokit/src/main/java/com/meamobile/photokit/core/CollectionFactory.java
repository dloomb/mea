package com.meamobile.photokit.core;

import com.meamobile.photokit.amazon.AmazonCollection;
import com.meamobile.photokit.deviant_art.DeviantArtCollection;
import com.meamobile.photokit.dropbox.DropboxCollection;
import com.meamobile.photokit.facebook.FacebookCollection;
import com.meamobile.photokit.flickr.FlickrCollection;
import com.meamobile.photokit.google.GoogleCollection;
import com.meamobile.photokit.instagram.InstagramCollection;
import com.meamobile.photokit.local.LocalCollection;
import com.meamobile.photokit.one_drive.OneDriveCollection;
import com.meamobile.photokit.photobucket.PhotobucketCollection;
import com.meamobile.photokit.core.Collection.CollectionType;
import com.meamobile.photokit.pinterest.PinterestCollection;
import com.meamobile.photokit.twitter.TwitterCollection;

import java.util.EnumSet;

public class CollectionFactory
{
    public static Collection BaseCollectionWithSourceTypes(EnumSet<CollectionType> types)
    {
        Collection collection = new Collection();

        if (types.contains(CollectionType.Local))
        {
            LocalCollection local = LocalCollection.RootCollection();
            collection.addCollection(local);
        }

        if (types.contains(CollectionType.Instagram))
        {
            InstagramCollection instagram = InstagramCollection.RootCollection();
            collection.addCollection(instagram);
        }

        if (types.contains(CollectionType.Facebook))
        {
            FacebookCollection facebook = FacebookCollection.RootCollection();
            collection.addCollection(facebook);
        }

        if (types.contains(CollectionType.Dropbox))
        {
            DropboxCollection dropbox = DropboxCollection.RootCollection();
            collection.addCollection(dropbox);
        }

        if (types.contains(CollectionType.Flickr))
        {
            FlickrCollection flickr = FlickrCollection.RootCollection();
            collection.addCollection(flickr);
        }

        if (types.contains(CollectionType.Google))
        {
            GoogleCollection google = GoogleCollection.RootCollection();
            collection.addCollection(google);
        }

        if (types.contains(CollectionType.Twitter))
        {
            TwitterCollection twitter = TwitterCollection.RootCollection();
            collection.addCollection(twitter);
        }

        if (types.contains(CollectionType.DeviantArt))
        {
            DeviantArtCollection deviant = DeviantArtCollection.RootCollection();
            collection.addCollection(deviant);
        }

        if (types.contains(CollectionType.Pinterest))
        {
            PinterestCollection pinterest = PinterestCollection.RootCollection();
            collection.addCollection(pinterest);
        }

        if (types.contains(CollectionType.OneDrive))
        {
            OneDriveCollection onedrive = OneDriveCollection.RootCollection();
            collection.addCollection(onedrive);
        }

        if (types.contains(CollectionType.Amazon))
        {
            AmazonCollection amazon = AmazonCollection.RootCollection();
            collection.addCollection(amazon);
        }

        if (types.contains(CollectionType.Photobucket))
        {
            PhotobucketCollection photobucket = PhotobucketCollection.RootCollection();
            collection.addCollection(photobucket);
        }

        return collection;
    }

}
