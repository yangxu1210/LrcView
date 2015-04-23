package com.example.view;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * 处理歌词的类
 * 
 * @author Administrator
 * 
 */
public class LrcProcess {

	private List<LrcContent> lrcList;// List集合存放歌词内容对象
	private LrcContent mLrcContent; // 声明一个歌词内容对象

	/**
	 * 无参构造函数用来实例化对象
	 */
	public LrcProcess() {
		mLrcContent = new LrcContent();
		lrcList = new ArrayList<LrcContent>();
	}

	/**
	 * 读取歌词
	 */
	public String parseLrc(InputStream inputStream) {
		// 定义一个StringBuilder对象，用来存放歌词内容
		StringBuilder stringBuilder = new StringBuilder();
		try {
			InputStreamReader sReader = new InputStreamReader(inputStream,
					"UTF-8");
			BufferedReader bReader = new BufferedReader(sReader);

			String str = "";
			while ((str = bReader.readLine()) != null) { // readLine读取一个文本行
				/**
				 * 歌词内容格式如下： [00:02.32]陈奕迅 [00:03.43]好久不见
				 */
				Log.e("", str);
				str = str.replace("[", "");
				str = str.replace("]", "@");// 如：00:03.43@好久不见

				// 分离@字符
				String splitLrcData[] = str.split("@"); // 降str这个字符串用“@”进行分割，分割后的字符串数组放在a[]中
														// 如：{00:03.43，好久不见}
				if (splitLrcData.length > 1) {
					mLrcContent.setLrcStr(splitLrcData[1]); // 设置歌词

					// 处理歌词取得歌曲的时间
					int lrcTime = timeStr(splitLrcData[0]); // 设置时间

					mLrcContent.setLrcTime(lrcTime);

					// 添加进列表数组
					lrcList.add(mLrcContent);

					// 新创建歌词内容对象
					mLrcContent = new LrcContent();
				}
			}

		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			stringBuilder.append("未找到歌词文件，赶紧去下载哦...");
		} catch (IOException e) {
			stringBuilder.append("未读取到歌词哦...");
		}

		return stringBuilder.toString();
	}

	/**
	 * 解析歌词时间
	 * 
	 * 传进来的timeStr如： 00:03.43
	 */
	public int timeStr(String timeStr) {
		timeStr = timeStr.replace(":", ".");
		timeStr = timeStr.replace(".", "@");

		String timeData[] = timeStr.split("@");// 将时间分割成字符串数组

		// 分离出 分、秒 并转换为整型
		int min = Integer.parseInt(timeData[0]);
		int sec = Integer.parseInt(timeData[1]);
		int millSec = Integer.parseInt(timeData[2]);

		// 计算上一行与下一行的时间转换为毫秒数
		int currentTime = (min * 60 + sec) * 1000 + millSec * 10;
		return currentTime;
	}

	public List<LrcContent> getLrcList() {
		return lrcList;
	}
}
