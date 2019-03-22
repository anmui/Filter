package filter;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

public class AverageFilter {
	
	
	//图像存储
	public static Boolean saveImage(BufferedImage productImage, String path){
        try{
            File outputFile = new File(path);
            if(!outputFile.exists()){
                outputFile.getParentFile().mkdirs();
                Boolean isSuccess = outputFile.createNewFile();
                if(!isSuccess){
                    return false;
                }
            }
 
            Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName( "jpeg" );
            ImageWriter writer = iter.next();
            ImageWriteParam iwp = writer.getDefaultWriteParam();
            iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            iwp.setCompressionQuality(1);
 
            writer.setOutput(ImageIO.createImageOutputStream(outputFile));
			writer.write(null, new IIOImage(productImage,null, null), iwp);
            writer.dispose();
 
            return true;
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
}
	
	//均值滤波
	public static int getAVEcolor(int x,int y,BufferedImage bi)
	{
		int color=0;
		int r=0,g=0,b=0;
		for(int i=x-1;i<=x+1;i++)
			for(int j=y-1;j<=y+1;j++)
			{
				color=bi.getRGB(i, j);
				r += (color >> 16) & 0xff;
	            g += (color >> 8) & 0xff;
	            b += color & 0xff;
			}
		 int ia = 0xff;  
         int ir = (int)(r/9);  
         int ig = (int)(g/9);  
         int ib = (int)(b/9);  
         color = (ia << 24) | (ir << 16) | (ig << 8) | ib;  
			return color;
	}
	
	
	//中值滤波
	public static int getMidcolor(int x,int y,BufferedImage bi)
	{
		int color=0;
		int m=0;
		int a[]=new int[9];
		for(int i=x-1;i<=x+1;i++)
			for(int j=y-1;j<=y+1;j++)
			{
				color=bi.getRGB(i, j);
				a[m]=color;
				m++;
			}
		Arrays.sort(a);
		color=a[5];
		
		return color;
	}
	
	//控制灰度值范围
	public static int clamp(int a)
	{
		if(a>255)
			a=255;
		if(a<0)
			a=0;
		return a;
	}
	
	//LPLS滤波中间权重为8
	public static int getLPLScolor8(int x,int y,BufferedImage bi)
	{
		int color=0,r=0,g=0,b=0;
		for(int i=x-1;i<=x+1;i++)
			for(int j=y-1;j<=y+1;j++)
			{
				if(i!=x&&j!=y)
				{
				color=bi.getRGB(i, j);
				r -= (color >> 16) & 0xff;
	            g -= (color >> 8) & 0xff;
	            b -= color & 0xff;
				}
				else if(i==x&&j==y)
				{
				color=bi.getRGB(i, j);
				r += 8*((color >> 16) & 0xff);
	            g += 8*((color >> 8) & 0xff);
	            b += 8*(color & 0xff);
				}
			}
		color=bi.getRGB(x, y);
		r += (color >> 16) & 0xff;
        g += (color >> 8) & 0xff;
        b += color & 0xff;
        int ia = 0xff;  
        
        color = (ia << 24) | (clamp(r) << 16) | (clamp(g) << 8) | clamp(b); 

        return color;
	}
	
	//Sobel滤波
	public static int getSobelcolor(int x,int y,BufferedImage bi)
	{
		int color=0;
		int r1=0,g1=0,b1=0;
		int r2=0,g2=0,b2=0;
		int []a1= {-1,-2,-1,0,0,0,1,2,1};
		int []a2= {1,0,-1,2,0,-2,1,0,-1};
		int m=0;
		for(int i=x-1;i<=x+1;i++)
			for(int j=y-1;j<=y+1;j++)
			{
				color=bi.getRGB(i, j);
				r1 += a1[m]*((color >> 16) & 0xff);
	            g1 += a1[m]*((color >> 8) & 0xff);
	            b1 += a1[m]*(color & 0xff);
	            r2 += a2[m]*((color >> 16) & 0xff);
	            g2 += a2[m]*((color >> 8) & 0xff);
	            b2 += a2[m]*(color & 0xff);
	            m+=1;
			}
		r1=(int)Math.sqrt(r1*r1+r2*r2);
		g1=(int)Math.sqrt(g1*g1+g2*g2);
		b1=(int)Math.sqrt(b1*b1+b2*b2);
		int ia = 0xff;  
		color = (ia << 24) | (clamp(r1) << 16) | (clamp(g1) << 8) | clamp(b1); 
		return color;
	}
	
	
	//LPLS中间权重为4
	public static int getLPLScolor4(int x,int y,BufferedImage bi)
	{
		int color=0,r=0,g=0,b=0;
		color=bi.getRGB(x, y+1);
		r -= (color >> 16) & 0xff;
        g -= (color >> 8) & 0xff;
        b -= color & 0xff;
        color=bi.getRGB(x-1, y);
		r -= (color >> 16) & 0xff;
        g -= (color >> 8) & 0xff;
        b -= color & 0xff;
        color=bi.getRGB(x+1, y);
		r -= (color >> 16) & 0xff;
        g -= (color >> 8) & 0xff;
        b -= color & 0xff;
        color=bi.getRGB(x, y-1);
		r -= (color >> 16) & 0xff;
        g -= (color >> 8) & 0xff;
        b -= color & 0xff;
        color=bi.getRGB(x, y);
		r += 5*((color >> 16) & 0xff);
        g += 5*((color >> 8) & 0xff);
        b += 5*(color & 0xff);
     
	    int ia = 0xff;  
	    color = (ia << 24) | (clamp(r) << 16) | (clamp(g) << 8) | clamp(b); 
		return color;
	}
	
	public static BufferedImage newBI(BufferedImage bi,int n)
	{
		int  width=bi.getWidth();
		int height=bi.getHeight();
		BufferedImage bf= new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int color = 0;
		for(int y=0;y<height;y+=1)
		{
			for(int x=0;x<width;x+=1)
			{
				if(x!=0&&y!=0&&x!=width-1&&y!=height-1)
				{
					if(n==1)
					{
						color=getAVEcolor(x,y,bi);
					}
					if(n==2)
						color=getMidcolor(x,y,bi);
					if(n==3)
						color=getLPLScolor4(x,y,bi);
					if(n==4)
						color=getLPLScolor8(x,y,bi);
					if(n==5)
						color=getSobelcolor(x,y,bi);
				}
				bf.setRGB(x, y, color);
			}
		}
		return bf;
	}
	
	public static void grayImage(String image,int a) throws IOException
	{
	File file = new File(image);
	int flag=0;
	BufferedImage bi = null;
	try
	{
		bi=ImageIO.read(file);
	}catch(Exception e)
	{
		e.printStackTrace();
	}
	if(a!=6)
	{
	bi=newBI(bi,a);
	saveImage(bi,"C:\\\\Users\\\\Mac\\\\Desktop\\\\result.jpeg");
	}
	else if(a==6)
	{
		for(int i=0;i<6;i++)
		{
			BufferedImage bi1=newBI(bi,i+1);
			StringBuffer bs=new StringBuffer();
			bs.append("C:\\\\Users\\\\Mac\\\\Desktop\\\\result");
			bs.append((char)(i+'a'));
			bs.append(".jpeg");
			String s1=bs.toString();
			System.out.println(bs.toString());
			saveImage(bi1,s1);
		}
	}
	
}
	
public static void main(String[] arg) throws IOException
{
	StringBuffer str=new StringBuffer();
	Scanner input=new Scanner(System.in);
	System.out.println("请输入图片地址：");
	String image=input.next();
	//String image="C:\\Users\\Mac\\Desktop\\5.jpg";
	System.out.println("请选择功能");
	System.out.println("1:均值滤波\n"+"2:中值滤波\n"+"3:拉普拉斯滤波重心权重为4");
	System.out.println("4:拉普拉斯滤波中心权重为8\n"+"5:Sobel滤波\n"+"6:所有输出");
	int n=0;
	n=input.nextInt();
	grayImage(image,n);
	System.out.println("完成！");
}

}
