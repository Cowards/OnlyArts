/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cowards.onlyarts.resources.v3;

import com.cowards.onlyarts.repositories.category.CategoryDTO;
import com.cowards.onlyarts.services.CategoryDAO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

/**
 *
 * @author Admin
 */
@Path("v3/categories")
public class Category {
    
    private static final CategoryDAO cateDao = CategoryDAO.getInstance();
    
    @GET
    @Path("/name/{name}")
    public Response getCateByName(@PathParam("name") String cateName) {
        try {
            CategoryDTO categoryDTO = cateDao.getCateByName(cateName);
            return Response.ok(categoryDTO).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }
}
