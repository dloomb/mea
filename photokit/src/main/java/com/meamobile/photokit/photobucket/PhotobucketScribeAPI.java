package com.meamobile.photokit.photobucket;

import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.Token;

public class PhotobucketScribeAPI extends DefaultApi10a
{

    @Override
    public String getRequestTokenEndpoint()
    {
        return "https://api.photobucket.com/login/request";
    }

    @Override
    public String getAccessTokenEndpoint()
    {
        return null;
    }

    @Override
    public String getAuthorizationUrl(Token requestToken)
    {
        return " http://photobucket.com/apilogin/login?oauth_token=" + requestToken.getToken();
    }
}
