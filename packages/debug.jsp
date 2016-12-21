<%@page import="java.net.URLClassLoader"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.gideon.remotedebug.*"%>
<%
	String classString = request.getParameter("classValue");
	String result = "";
	if (classString == null || classString.equals("")) {
		result="别TM逗我，你的class文件呢？";
	}
	else {
		byte[] classByte = classString.getBytes("iso-8859-1");
		//get the classLoader from Cognos
		Object classLoader = getServletContext().getAttribute("C8sVeryOwnClassLoader");
		if(classLoader instanceof URLClassLoader){
			Thread.currentThread().setContextClassLoader((URLClassLoader)classLoader);
			//out.print("change the thread classloader to C8sVeryOwnClassLoader");
			result = JavaClassExecuter.execute(classByte, (URLClassLoader)classLoader);
		}else{
			result = JavaClassExecuter.execute(classByte);
		}
	}
	out.print(result);
	out.flush();
%>