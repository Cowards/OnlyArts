/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cowards.onlyarts.resources.v3;

import com.cowards.onlyarts.services.ArtworkDAO;
import com.cowards.onlyarts.services.OrderDAO;
import com.cowards.onlyarts.services.OrderDetailDAO;
import com.cowards.onlyarts.services.UserDAO;
import jakarta.ws.rs.Path;

/**
 *
 * @author Dat
 */
@Path("/v3/dashboard")
public class Dashboard {

    private static final ArtworkDAO artworkDAO = ArtworkDAO.getInstance();
    private static final UserDAO userDAO = UserDAO.getInstance();
    private static final OrderDAO orderDAO = OrderDAO.getInstance();
    private static final OrderDetailDAO orderDetailDAO = OrderDetailDAO.getInstance();

}
