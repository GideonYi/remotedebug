package com.gideon.remotedebug;

/**
 * 为了多次载入执行类而加入的加载器<br>
 * 把defineClass方法开放出来，只有外部显式调用的时候才会使用到loadByte方法
 * 由虚拟机调用时，仍然按照原有的双亲委派规则使用loadClass方法进行类加载
 *
 * @author zzm
 * @author Gideon
 */
public class HotSwapClassLoader extends ClassLoader {

    /**
	 * this flag used for cognos classloader
	 */
	public static String DEBUG_PACKAGE_COGNOS = "cognos";
	/**
	 * this flag is a little trick as switch,change it as you like 
	 */
	public static String DEBUG_PACKAGE_FLAG = "gideon";


	public HotSwapClassLoader() {
        super(HotSwapClassLoader.class.getClassLoader());
    }
    
    public HotSwapClassLoader(ClassLoader parent){
    	super(parent);
    }
    
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
    	if(name==null || "".equals(name)){
    		ClassNotFoundException cnfe= new ClassNotFoundException("invaild Class name!");
    		throw cnfe;
    	}else{
    		// used for the Cognos10.1.1 run in Tomcat6
    		if(this.getParent().toString().contains(DEBUG_PACKAGE_COGNOS) && name.contains(DEBUG_PACKAGE_FLAG)){
    			// getParent() twice because of cognos doesn't use the default WebAppClassloader to load cognos class
    			// but the user thread use the  WebAppClassloader, 
    			// the add 
    			return getParent().getParent().loadClass(name);
    		}
    	}
    	return super.loadClass(name);
    }
    

    public Class loadByte(byte[] classByte) {
        return defineClass(null, classByte, 0, classByte.length);
    }

}

