package org.kkb.server.api.restassured.LcmsCourse;


import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kkb.server.api.TestConfig;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;

/**
 * Created by www on 2015/7/17.
 */
public class CourseGetTest {

	  static Integer Course_id=0;
	    String access_token=TestConfig.getTokenbyUserID();
	    static int intro_id1=0;
	    static int intro_id2=0;
	    //创建两个老师
	    @BeforeClass
	    public void addCourse(){
	        JSONObject jsonObject=new JSONObject();
	        JSONObject jsonObject_sub = new JSONObject();
	        JSONArray jsonArray = new JSONArray();
	        
	        jsonObject.put("name", "API自动测试讲师1");
	        jsonObject.put("title", "北京航空航天大学经济管理学院讲师、副教授");
	        jsonObject.put("intro", "北京航空航天大学经济管理学院讲师、副教授，北航EMBA教育中心常务副主任、北航MBA教育中心主任、北航中澳国际MBA项目主任经管学院学生职业发展中心主任");
	        jsonObject.put("desc", "");
	        jsonObject.put("avatar","234.jpg");
	        jsonObject.put("sina_weibo", "");
	        jsonObject.put("tag", "");
	        jsonObject.put("tx_weibo", "");

	        Response response = TestConfig.postOrPutExecu("post", "/tenants/1/instructors?access_token=" + access_token, jsonObject);
	        response.then().
	                log().all().
	                assertThat().statusCode(200).body("message", equalTo("success"));

	        String body=response.body().asString();

	        intro_id1= JsonPath.with(body).get("data");
	        if (intro_id1==0 ) throw new AssertionError();

	        jsonObject.put("name", "API自动测试讲师2");
	        jsonObject.put("title", "北京工业大学经济管理学院讲师、副教授");
	        jsonObject.put("intro", "北京工业大学经济管理学院讲师、副教授，北工大EMBA教育中心常务副主任、北工大MBA教育中心主任、北工大中澳国际MBA项目主任经管学院学生职业发展中心主任");
	        jsonObject.put("desc", "");
	        jsonObject.put("avatar","234.jpg");
	        jsonObject.put("sina_weibo", "");
	        jsonObject.put("tag", "");
	        jsonObject.put("tx_weibo", "");

	        response = TestConfig.postOrPutExecu("post", "/tenants/1/instructors?access_token=" + access_token, jsonObject);
	        response.then().
	                log().all().
	                assertThat().statusCode(200).body("message", equalTo("success"));

	        body=response.body().asString();

	        intro_id2= JsonPath.with(body).get("data");
	        if (intro_id2==0 ) throw new AssertionError();
	        
	        jsonObject_sub.put("id", intro_id1);
	        jsonArray.add(jsonObject_sub);
	        jsonObject_sub.put("id", intro_id2);
	        jsonArray.add(jsonObject_sub);
	        jsonObject.clear();
	        jsonObject.put("name","API自动化测试课程，根据id查询");
	        jsonObject.put("intro","第八章 API自动化测试");
	        jsonObject.put("desc", "本课程介绍了阿里云产品基础架构、ECS 产品概念和功能、ECS运维管理和API编程接口。");
	        jsonObject.put("instructorList", jsonArray);
	        response = TestConfig.postOrPutExecu("post", "/courses?access_token="+access_token, jsonObject);
	        response.then().
	        	//log().all().
	                assertThat().statusCode(200).body("status", equalTo(true)).
	                body("message", equalTo("success"));

	         body = response.body().asString();
	         //返回创建课程的id
	        Course_id = JsonPath.with(body).get("data");
	        if (Course_id == 0) throw new AssertionError();
	    }
			 //正常查询
		    @Test(priority=1)
		    public void testGetCourseWithCorrectToken() {
		        Response response = TestConfig.getOrDeleteExecu("get", "courses?access_token=" + access_token );
		        List arry = new ArrayList();
		        arry.add(intro_id1);
		        arry.add(intro_id2);
		        response.then().
		                log().all().
		                assertThat().statusCode(200).body("status", equalTo(true)).
		                body("message", equalTo("success")).
		                body("data.pageList.id",Matchers.hasItem(Course_id)).
		                body("data.pageList.instructorList.id",Matchers.hasItem(arry));
		               
		
		    }

	    	//token不能为空
	        @Test(priority = 2)
	        public void testGetCourseWithoutToken () {
	
	            Response response = TestConfig.getOrDeleteExecu("get", "/courses?access_token=");
	            response.then().
	                    assertThat().statusCode(400).body("status", equalTo(false)).
	                    body("message", equalTo("token不能为空"));
	        }


        	//无效的access_token
            @Test(priority = 3)
            public void testGetCourseWithIncorrectToken () {

                Response response = TestConfig.getOrDeleteExecu("get", "/courses?access_token=f548b508f351231c6f6c8efa2cd44531");
                response.then().
                        assertThat().statusCode(401).body("status", equalTo(false)).
                        body("message", equalTo("无效的access_token"));
            }

            //翻页
            @Test(priority = 4)
            public void testGetByPagesCourse () {
                Response response = TestConfig.getOrDeleteExecu("get", "courses?access_token=" + access_token + "&curPage=2&pageSize=10");
                response.then().
                        log().all().
                        assertThat().statusCode(200).body("status", equalTo(true)).
                        body("message", equalTo("success")).
                        body("data.curPage",equalTo(2)).
                        body("data.pageSize",equalTo(10)).
                        body("data.startRow",equalTo(10)).
                        body("data.endRow",equalTo(20));
            }


    }