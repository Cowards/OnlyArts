package com.cowards.onlyarts.resources.v4;

import com.cowards.onlyarts.services.FavorDAO;
import com.cowards.onlyarts.services.TokenDAO;
import com.cowards.onlyarts.services.UserDAO;
import jakarta.ws.rs.Path;

@Path("v4/favor")
public class Favor {

    private static final FavorDAO favorDao = FavorDAO.getInstance();
    private static final TokenDAO tokenDao = TokenDAO.getInstance();
    private static final UserDAO userDao = UserDAO.getInstance();

}
