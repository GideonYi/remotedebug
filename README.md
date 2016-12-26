# introduction
This is a tool for developer to debug remote system in anytime without breakpoints using eclipse. The tool is intend to used by java developers who use eclipse to develop program.  It only provides a POC without much feature.
This introduction will explain how it works, and the key points. So you can extend it in your own situation.
#how to use it
##use it with packages
This is the easier way to use this plugin. You only need few steps to make it run.
1. Copy the jar file remotedebug-eclipse-plugin_1.0.0.201612211345.jar into eclipse folder eclipse_home/dropins,the restart it. (I develop this plugin in eclipse kepler, so it only supports eclipse version equals or higher than kepler)
2. You should see a new preference item with name RemoteDebug in the eclipse Preferences view.
3. You will see four options, a checkbox to switch whether use the class path in the second file field. The second checkbox is used for whether show the debug messages, such as the class path you send to the server. The last text field is the URL to accept the class bytes. You can copy the debug.jsp file in the folder packages to %WEB_APP_ROOT%/WebContent/debug.jsp, then you can set the URL to http://%HOSTNAME%:%PORT%/%WEB_APP_NAME%/debug.jsp.
4. After you have config the eclipse, the last step is to copy the file  remote-debug-0.0.2-SNAPSHOT.jar in folder packages to the path WebContent/WEB-INF/lib/, so the debug.jsp can find the classes it needs.
##use it with sources
All the sources code are in the sources folder, including three eclipse project. The remotedebug project provides the jar needed by debug.jsp. The remotedebug-eclipse-plugin project is the source code of eclipse plugin. The WebDemo is a demo for you to refer to. All projects are in maven structure, so if you want to customize the tool, you should to be familiar with maven.
#how it works inside
The tool works together with two parts, the server part and the eclipse plugin part. 
## the server side
When we debug, we can't say we can figure things out in one time, so we can't make sure we only load the class once.So a request is we need to load a signal class in multiple times. The other request is not to mess up the logs in the server.
### the process to load class from network
The core code in debug.jsp is as bellow:
```
String classString = request.getParameter("classValue");
byte[] classByte = classString.getBytes("iso-8859-1");
result = JavaClassExecuter.execute(classByte);
```
get class String value from parameters, convert it to bytes, and then execute it. The core code of execute method is as below:
```
try {
	Method method = clazz.getMethod("main", new Class[] { String[].class });
	method.invoke(null, new String[] { null });
} catch (Throwable e) {
	e.printStackTrace(HackSystem.out);
}
```
Create a new method instance of Main method from class instance, and then execute it. 
### how to load a class in mutiple times
The key to load a class multiple time is using a different classloader everytime, so the JVM will think the class hasn't load yet. Then we can change the class bytes hotly.
```
HotSwapClassLoader loader = new HotSwapClassLoader();
Class clazz = loader.loadByte(modiBytes);
```
```
public Class loadByte(byte[] classByte) {
	return defineClass(null, classByte, 0, classByte.length);
}
```
### how to not mess up the log in server side
When we debug in eclipse with breakpoints, we can get the value in the variables view. But using this tool, we don't use the breakpoint, we get the value throught code, such as xxx.getValue(). And then we need to print the result, but we use the System.out.println(). If we use in the default way, the message will print out in console and log files. The trick is __hack__ the System class with own defined HackSystem, it redefined the default out console to a PrintStream. Then get the value from the new out, return it to the caller.
```
public class HackSystem {
public final static PrintStream out = new PrintStream(buffer);
public static String getBufferString() {
        return buffer.toString();
    }
}
``` 
## the eclipse plugin
In eclipse, we write our debug code in a executable class with main method. Then compile the class, so the class file is stored in our disk, then get the bytes from the class file path. Send the bytes throught http. Print the http response in the console. All done.
eclipse handler key code:
```
InputStream is = new FileInputStream(classPath);
byte[] b = new byte[is.available()];
is.read(b);
is.close();
String classValue = new String(b, "iso-8859-1");
String result = HttpUtils.postRequest(debugURL, classValue);
Activator.getStream().println(result);
```
http method:
```
public static String postRequest(String url, String classValue) throws UnsupportedEncodingException, IllegalStateException, IOException {
	HttpPost httppost = new HttpPost(url);
	// post 参数 传递
	List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
	nvps.add(new BasicNameValuePair("classValue", classValue)); // 参数

	httppost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8)); // 设置参数给Post

	// 执行
	HttpResponse response = httpclient.execute(httppost);
	HttpEntity entity = response.getEntity();
	if (entity != null) {
		// System.out.println("Response content length: " +
		// entity.getContentLength());
		// 显示结果
		BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));

		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			//skip the empty line, maybe different in different OS
			if (!line.trim().isEmpty()) {
				stringBuilder.append(line);
				stringBuilder.append("\r\n");
			}
		}
		reader.close();
		return stringBuilder.toString();
	}
	else {
		return "return entity is null";
	}
}
```