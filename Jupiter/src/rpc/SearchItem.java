package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import external.TicketMasterClient;
import java.util.List;
import java.util.Set;

import entity.item;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}
		
		String userId = session.getAttribute("user_id").toString();
		// optional
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		
		String term = request.getParameter("term");
		DBConnection connection = DBConnectionFactory.getConnection();
         try {
        	 List<item> items = connection.searchItems(lat, lon, term);
        	 Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);
        	 
        	 JSONArray array = new JSONArray();
        	 for (item item : items) {
        		 array.put(item.toJSONObject());
        		 
 				JSONObject obj = item.toJSONObject();//找到用户设置过的favorite items
 				obj.put("favorite", favoritedItemIds.contains(item.getItemId())); //看看当前搜索的东西是不是favorite item，是的话在这个东西后面加个心形以提示用户
 				array.put(obj);        		
        	 }
        	 Helper.writeJsonArray(response, array);
	
            } catch (Exception e) {
            		e.printStackTrace();
            } finally {
            		connection.close();
            }


	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		HttpSession session = request.getSession(false); // 看sessionID是否还存在，不存在就返回403
		if (session == null) {
			response.setStatus(403);
			return;
		}
		
		doGet(request, response);
	}

}
