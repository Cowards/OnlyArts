package com.cowards.onlyarts.resources.v3;

import com.cowards.onlyarts.services.ArtworkDAO;
import com.cowards.onlyarts.services.CartDAO;
import com.cowards.onlyarts.services.OrderDAO;
import com.cowards.onlyarts.services.OrderDetailDAO;
import com.cowards.onlyarts.services.TokenDAO;
import com.cowards.onlyarts.services.UserDAO;
import jakarta.ws.rs.Path;

@Path("/v3/orders")
public class Order {

    private static final OrderDAO orderDao = OrderDAO.getInstance();
    private static final OrderDetailDAO orderDetailDao = OrderDetailDAO.getInstance();
    private static final TokenDAO tokenDao = TokenDAO.getInstance();
    private static final UserDAO userDao = UserDAO.getInstance();
    private static final CartDAO cartDao = CartDAO.getInstance();
    private static final ArtworkDAO artworkDao = ArtworkDAO.getInstance();

}
