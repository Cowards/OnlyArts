package com.cowards.onlyarts.resources.v2;

import com.cowards.onlyarts.services.FavorDAO;
import com.cowards.onlyarts.services.TokenDAO;
import jakarta.ws.rs.Path;

@Path("v2/favor")
public class Favor {

    private static final FavorDAO favorDao = FavorDAO.getInstance();
    private static final TokenDAO tokenDao = TokenDAO.getInstance();

}
