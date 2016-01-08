package com.meamobile.photokit.dropbox;

import android.annotation.SuppressLint;

import com.dropbox.core.v2.DbxFiles.FileMetadata;
import com.dropbox.core.v2.DbxFiles.MediaMetadata;
import com.dropbox.core.v2.DbxSharing.LinkMetadata;

import com.meamobile.photokit.core.RemoteAsset;

import java.util.List;


@SuppressLint("ParcelCreator")
public class DropboxAsset extends RemoteAsset
{
    private String  mDropboxId;

    public DropboxAsset(FileMetadata data, List<LinkMetadata> links)
    {
        MediaMetadata media = data.mediaInfo.getMetadata();

        mDropboxId = data.id;
        mTitle = data.name;
//        mTimestamp = media.timeTaken.getTime();

        String url = links.get(0).url;
        url = url.replace("?dl=0", "?dl=1");

        mFullResolutionUrlString = url;
        mThumbnailUrlString = mFullResolutionUrlString;
    }

}
