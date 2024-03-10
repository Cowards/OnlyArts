package com.cowards.onlyarts.resources.v4;

import com.cowards.onlyarts.services.FollowDAO;
import com.cowards.onlyarts.services.TokenDAO;
import jakarta.ws.rs.Path;

@Path("v4/follow")
public class Follow {

    private static final TokenDAO tokenDao = TokenDAO.getInstance();
    private static final FollowDAO followDao = FollowDAO.getInstance();

}
