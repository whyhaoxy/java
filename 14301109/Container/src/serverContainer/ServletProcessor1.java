package serverContainer;



import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import serverContainer.Constants;
import serverContainer.Request;
import serverContainer.Response;

public class ServletProcessor1 {

    public void process(Request request, Response response) {

        String uri = request.getUri();
        String servletName = uri.substring(uri.lastIndexOf("/") + 1);
        String servletMapping=null;
       
        //类加载器，用于从指定JAR文件或目录加载类
       /* URLClassLoader loader = null;
        try {
            URLStreamHandler streamHandler = null;
            //创建类加载器
            loader = new URLClassLoader(new URL[]{new URL(null, "file:" + Constants.WEB_SERVLET_ROOT, streamHandler)});
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        
        //Servlet servlet = loadServlet(servletName);
        
        Class<?> myClass = null;
        try {
            //加载对应的servlet类
            myClass = loader.loadClass(servletName);
        } catch (ClassNotFoundException e) {
            System.out.println(e.toString());
        }

        Servlet servlet = null;

        try {
            //生产servlet实例
            servlet = (Servlet) myClass.newInstance();
            //执行servlet的service方法
            servlet.service((ServletRequest) request,(ServletResponse) response);
        } catch (Exception e) {
            System.out.println(e.toString());
        } catch (Throwable e) {
            System.out.println(e.toString());
        }*/

    }
    
    public static void processServletRequest(Request req, 
			Response res) throws Exception{
    	 String uri = req.getUri();
         String serv = uri.substring(uri.lastIndexOf("/") + 1);
         serv="/"+serv;
         System.out.println("解析"+serv);
		//解析 web.xml , 根据uri得到servlet路径
		String servletName = getServerName(serv);
		
		System.out.println("Processing servlet: " + servletName);
		//加载servlet类
		Servlet servlet = loadServlet(servletName);
		//将request和response交给Servlet处理
		callService(servlet, req, res);
	}
    
    private static Servlet loadServlet(String servletName) throws MalformedURLException {
		String servletURL = "../" + servletName.replace('.', '/');
		
		File file = new File(servletURL);
		//URL url = new URL("file://Servlet/LoginServlet");
		URL url = file.toURL();
		URLClassLoader loader = new URLClassLoader(
				new URL[] { url }, Thread.currentThread().getContextClassLoader());
		Servlet servlet = null;
		
		try {
			@SuppressWarnings("unchecked")
			Class<Servlet> servletClass = (Class<Servlet>) loader
					.loadClass(servletName);
			servlet = (Servlet) servletClass.newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return servlet;
	}
    
    private static void callService(Servlet servlet, ServletRequest request,
			ServletResponse response) {
		try {
			servlet.service(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    private static String getServerName(String uri)throws Exception{
    		String servletName = null;
    		
    		//parse web.xml
    		SAXReader reader = new SAXReader();
    		Document doc = reader.read(new File("web.xml"));
    		//get root element ---<web-app></web-app>
    		Element node = doc.getRootElement();
    		System.out.println(node.getName());
    		//List<Attribute> list = node.attributes();
    		String servlet = null;
    		Iterator<Element> it = node.elementIterator("servlet-mapping");
    		
    		while(it.hasNext()){
    			Element e = it.next();
    			Iterator<Element> ite = e.elementIterator("url-pattern");
    			Iterator<Element> ite1 = e.elementIterator("servlet-name");
    			if(ite.hasNext()){
    				Element el = ite.next();
    				if(el.getText().equals(uri)){
    					servlet = ite1.next().getText();
    					break;
    				}
    			}
    		}
    		
    		Iterator<Element> it1 = node.elementIterator("servlet");
    		
    		while(it1.hasNext()){
    			Element e = it1.next();
    			Iterator<Element> iter = e.elementIterator("servlet-name");
    			Iterator<Element> iter1 = e.elementIterator("servlet-class");
    			
    			if(iter.hasNext()&&iter.next().getText().equals(servlet)){
    				servletName = iter1.next().getText();
    				System.out.println(servletName);
    				break;
    			}
    			
    		}
    		return servletName;
    	}
}