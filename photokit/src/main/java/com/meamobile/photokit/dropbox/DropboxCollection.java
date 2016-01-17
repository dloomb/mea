package com.meamobile.photokit.dropbox;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.webkit.MimeTypeMap;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.DbxFiles;
import com.dropbox.core.v2.DbxFiles.Metadata;
import com.dropbox.core.v2.DbxFiles.FolderMetadata;
import com.dropbox.core.v2.DbxFiles.FileMetadata;

import com.dropbox.core.v2.DbxSharing;
import com.meamobile.photokit.core.Collection;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

@SuppressLint("ParcelCreator")
public class DropboxCollection extends Collection
{
    String mDropboxPath = "";

    private DropboxCollection() {}

    private DropboxCollection(FolderMetadata data, DropboxSource source)
    {
        mTitle = data.name;
        mSource = source;
        mDropboxPath = data.pathLower;
    }

    public static DropboxCollection RootCollection()
    {
        DropboxCollection collection = new DropboxCollection();

        collection.mSource = new DropboxSource();
        collection.mTitle = collection.mSource.getTitle();

        return collection;
    }

    @Override
    public CollectionType getType()
    {
        return CollectionType.Dropbox;
    }


    @Override
    public Observable<Double> loadContents(Activity activity)
    {
        return Observable.create(new Observable.OnSubscribe<Double>() {
            @Override
            public void call(Subscriber<? super Double> subscriber) {
                mLoadSubscriber = subscriber;

                DropboxSource source = (DropboxSource) mSource;
                DbxClientV2 client = source.getNewClient();

                try
                {
                    DbxFiles.ListFolderResult result = client.files.listFolderBuilder(mDropboxPath).includeMediaInfo(true).start();
                    ArrayList<Metadata> entries = result.entries;//client.files.listFolder(mDropboxPath).entries;
                    handleResponseEntries(entries);
                }
                catch (DbxException e)
                {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    private void handleResponseEntries(List<Metadata> entries)
    {
        for (Metadata metadata : entries)
        {
            //Collection
            if (metadata instanceof FolderMetadata)
            {
                DropboxCollection collection = new DropboxCollection((FolderMetadata) metadata, (DropboxSource) mSource);
                addCollection(collection);
            }
            //Asset
            else
            {
                final FileMetadata fileMetadata = (FileMetadata) metadata;
                new Thread(new Runnable() { @Override public void run()
                {
                    try
                    {
                        loadAssetFromMetadata(fileMetadata);
                    }
                    catch (DbxException e)
                    {
                        e.printStackTrace();
                    }
                }}).start();
            }

        }
    }

    private void loadAssetFromMetadata(FileMetadata metadata) throws DbxException
    {
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String ext = metadata.name.substring(metadata.name.indexOf(".") + 1);
        String type = mime.getMimeTypeFromExtension(ext);

        DbxClientV2 client = ((DropboxSource) mSource).getNewClient();
        DbxSharing.GetSharedLinksResult res = client.sharing.getSharedLinks(metadata.pathLower);
        List links = res.links;

        if (type != null && type.startsWith("image/") && metadata.mediaInfo != null && links.size() > 0)
        {
            DropboxAsset asset = new DropboxAsset(metadata, links);
            addAsset(asset);
        }
    }
}
