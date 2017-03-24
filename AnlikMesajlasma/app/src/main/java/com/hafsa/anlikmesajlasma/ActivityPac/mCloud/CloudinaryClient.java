package com.hafsa.anlikmesajlasma.ActivityPac.mCloud;

import com.cloudinary.Transformation;

import com.cloudinary.Cloudinary;

/**
 * Created by Hafsa on 24.03.2017.
 */
public class CloudinaryClient {


    public static String getUrl()
    {
        Cloudinary cloud=new Cloudinary(MyCon.getCon());

        Transformation t=new Transformation();
        t.radius(60);

        return  cloud.url().transformation(t).generate("asha.jpg");

    }

    public static String resize()
    {
        Cloudinary cloud=new Cloudinary(MyCon.getCon());

        Transformation t=new Transformation();
        t.width(300);
        t.height(250);

        return  cloud.url().transformation(t).generate("asha.jpg");

    }

}
