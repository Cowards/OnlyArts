package com.cowards.onlyarts.resources.v2;

import com.cowards.onlyarts.services.ReportDAO;
import com.cowards.onlyarts.services.TokenDAO;
import jakarta.ws.rs.Path;

@Path("v2/report/artworks")
public class Report {

    public static final TokenDAO tokenDao = TokenDAO.getInstance();
    public static final ReportDAO reportDao = ReportDAO.getInstance();

}
