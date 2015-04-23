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
 * �����ʵ���
 * 
 * @author Administrator
 * 
 */
public class LrcProcess {

	private List<LrcContent> lrcList;// List���ϴ�Ÿ�����ݶ���
	private LrcContent mLrcContent; // ����һ��������ݶ���

	/**
	 * �޲ι��캯������ʵ��������
	 */
	public LrcProcess() {
		mLrcContent = new LrcContent();
		lrcList = new ArrayList<LrcContent>();
	}

	/**
	 * ��ȡ���
	 */
	public String parseLrc(InputStream inputStream) {
		// ����һ��StringBuilder����������Ÿ������
		StringBuilder stringBuilder = new StringBuilder();
		try {
			InputStreamReader sReader = new InputStreamReader(inputStream,
					"UTF-8");
			BufferedReader bReader = new BufferedReader(sReader);

			String str = "";
			while ((str = bReader.readLine()) != null) { // readLine��ȡһ���ı���
				/**
				 * ������ݸ�ʽ���£� [00:02.32]����Ѹ [00:03.43]�þò���
				 */
				Log.e("", str);
				str = str.replace("[", "");
				str = str.replace("]", "@");// �磺00:03.43@�þò���

				// ����@�ַ�
				String splitLrcData[] = str.split("@"); // ��str����ַ����á�@�����зָ�ָ����ַ����������a[]��
														// �磺{00:03.43���þò���}
				if (splitLrcData.length > 1) {
					mLrcContent.setLrcStr(splitLrcData[1]); // ���ø��

					// ������ȡ�ø�����ʱ��
					int lrcTime = timeStr(splitLrcData[0]); // ����ʱ��

					mLrcContent.setLrcTime(lrcTime);

					// ��ӽ��б�����
					lrcList.add(mLrcContent);

					// �´���������ݶ���
					mLrcContent = new LrcContent();
				}
			}

		} catch (FileNotFoundException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
			stringBuilder.append("δ�ҵ�����ļ����Ͻ�ȥ����Ŷ...");
		} catch (IOException e) {
			stringBuilder.append("δ��ȡ�����Ŷ...");
		}

		return stringBuilder.toString();
	}

	/**
	 * �������ʱ��
	 * 
	 * ��������timeStr�磺 00:03.43
	 */
	public int timeStr(String timeStr) {
		timeStr = timeStr.replace(":", ".");
		timeStr = timeStr.replace(".", "@");

		String timeData[] = timeStr.split("@");// ��ʱ��ָ���ַ�������

		// ����� �֡��� ��ת��Ϊ����
		int min = Integer.parseInt(timeData[0]);
		int sec = Integer.parseInt(timeData[1]);
		int millSec = Integer.parseInt(timeData[2]);

		// ������һ������һ�е�ʱ��ת��Ϊ������
		int currentTime = (min * 60 + sec) * 1000 + millSec * 10;
		return currentTime;
	}

	public List<LrcContent> getLrcList() {
		return lrcList;
	}
}
