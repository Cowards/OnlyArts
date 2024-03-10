package com.cowards.onlyarts.resources.v1;

import com.cowards.onlyarts.services.ImageDAO;
import jakarta.ws.rs.Path;

@Path("v1/image")
public class Image {

    private static final ImageDAO imageDao = ImageDAO.getInstance();

}
