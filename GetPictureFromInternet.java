package com.example.lenovo;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	protected static final int SUCCESS = 1;

	protected static final int ERROR = 3;

	protected static final int FAILD = 2;
	private EditText et_path;
	private ImageView iv;
	private ProgressDialog pd;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			switch (msg.what) {
			case SUCCESS:
				Toast.makeText(MainActivity.this, "接收成功", 0).show();
				Bitmap bitmap=(Bitmap) msg.obj;
				iv.setImageBitmap(bitmap);
				break;
			case FAILD:
				Toast.makeText(MainActivity.this, "路径错误", 0).show();
				break;
			case ERROR:
				Toast.makeText(MainActivity.this, "网络错误，或路径有误。", 0).show();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		et_path = (EditText) findViewById(R.id.et_path);
		iv = (ImageView) findViewById(R.id.iv);
	}

	public void getPhoto(View View) {
		// 获取文本的内容
		final String path = et_path.getText().toString().trim();
		if (TextUtils.isEmpty(path)) {
			Toast.makeText(this, "请输入路径", 0).show();
			return;
		}
		pd=new ProgressDialog(this);
		pd.setMessage("正在加载");
		pd.show();
		new Thread() {
			public void run() {
				try {
					// 得到图片的URL路径
					URL url = new URL(path);
					// 通过路径打开这个链接
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					// 设置服务器的访问信息

					conn.setConnectTimeout(5000);
					conn.setRequestMethod("GET");
					// 获取服务器响应的信息状态码
					int type = conn.getResponseCode();
					if (type == 200) {
						// 获取流
						InputStream is = conn.getInputStream();
						// 接收流
						Bitmap bitmap = BitmapFactory.decodeStream(is);
						// 发送消息
						Message msg = Message.obtain();
						msg.what = SUCCESS;
						msg.obj = bitmap;
						handler.sendMessage(msg);
						is.close();
					} else{
						Message msg = Message.obtain();
						msg.what = FAILD;
						handler.sendMessage(msg);
						
					}
				} catch (Exception e) {
					e.printStackTrace();
					Message msg = Message.obtain();
					msg.what = ERROR;
					handler.sendMessage(msg);
					
				}
			};
		}.start();
	}

}
