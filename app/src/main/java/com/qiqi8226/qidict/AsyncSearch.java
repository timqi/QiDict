package com.qiqi8226.qidict;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by qiqi on 14-12-24.
 */
public class AsyncSearch extends AsyncTask<String, Void, String> {
    public static final int RESULT_ERROR = 1847;
    public static final int RESULT_OK = 1837;
    public static final int RESULT_HIDDEN_PROGRESS = 3425;
    public static final int RESULT_COMPLETE = 34534;

    private OnQueryComplete mOnQueryComplete;
    private HttpClient httpClient;
    private StringBuilder mStringBuilder = new StringBuilder();

    private boolean isTrans = false;
    private boolean isEnToZh = false;

    //请到 “百度翻译api” 免费注册你的app key
    private String urlPre = "http://openapi.baidu.com/public/2.0/translate/dict/simple?client_id=/*YOUR APP KEY*/&q=";
    private String urlTranPre = "http://openapi.baidu.com/public/2.0/bmt/translate?client_id=/*YOUR APP KEY*/&q=";

    private String urlTranNextEnToZh = "&from=en&to=zh";
    private String urlTranNextZhToEn = "&from=zh&to=en";

    public AsyncSearch(OnQueryComplete queryComplete, boolean isSimpleMode, boolean isEnToZh) {
        this.mOnQueryComplete = queryComplete;
        this.httpClient = new DefaultHttpClient();
        this.isEnToZh = isEnToZh;
        if (isSimpleMode) isTrans = true;
    }

    public interface OnQueryComplete {
        public void forResult(int resultCode, String result);
    }

    private void parseJSONAndUpdate(String s, String st) throws JSONException {
        JSONObject mainObject = new JSONObject(s);
        if (isTrans) {
            if (mainObject.has("error_code")) {
                mStringBuilder.append("没有找到相关翻译！\n");
                return;
            }
            mStringBuilder.append(mainObject.getJSONArray("trans_result")
                    .getJSONObject(0).getString("dst"));

        } else {
            int errorCode = mainObject.getInt("errno");
            if (errorCode != 0) {
                mStringBuilder.append("没有找到相关翻译！\n");
                return;
            }
            JSONObject data = null;
            try {
                data = mainObject.getJSONObject("data");
            } catch (JSONException e) {
                this.cancel(true);
                new AsyncSearch(mOnQueryComplete, true, false).execute(st);
            }
            JSONArray symbols = data.getJSONArray("symbols");
            JSONObject symbolsData = symbols.getJSONObject(0);
            if (isEnToZh) {
                String ph_am = symbolsData.getString("ph_am");
                String ph_en = symbolsData.getString("ph_en");
                if (ph_am.length() > 0) {
                    mStringBuilder.append("美音:[" + ph_am + "]");
                    if (ph_en.length() > 0)
                        mStringBuilder.append("  英音:[" + ph_en + "]\n\n");
                } else if (ph_en.length() > 0) mStringBuilder.append("英音:[" + ph_en + "]\n\n");
            }
            JSONArray parts = symbolsData.getJSONArray("parts");
            for (int i = 0; i < parts.length(); i++) {
                JSONObject part = parts.getJSONObject(i);
                if (isEnToZh) mStringBuilder.append("词性: " + part.getString("part") + '\n');
                JSONArray means = part.getJSONArray("means");
                for (int j = 0; j < means.length(); j++) {
                    mStringBuilder.append((j + 1) + ":" + means.getString(j) + '\n');
                }
                if (i == parts.length() - 1) break;
                mStringBuilder.append('\n');
            }

        }
    }

    @Override
    protected String doInBackground(String... strings) {
        String tmp, reuse;
        reuse = tmp = strings[0].trim();
        if (tmp.isEmpty())
        return mStringBuilder.append("请输入需要翻译的内容！\n").toString();
        try {
            if (!isTrans) isTrans = tmp.contains(" ");
            tmp = URLEncoder.encode(strings[0].trim(), "utf-8");
            if (isTrans) tmp = urlTranPre + tmp;
            else tmp = urlPre + tmp;
            if (isEnToZh) tmp = tmp + urlTranNextEnToZh;
            else tmp = tmp + urlTranNextZhToEn;
            HttpResponse response = httpClient.execute(new HttpGet(tmp));
            tmp = EntityUtils.toString(response.getEntity());
            if (isCancelled()) return null;
            parseJSONAndUpdate(tmp, reuse);
        } catch (UnsupportedEncodingException e) {
            mStringBuilder.append("不支持URL编码，请更换查询内容！");
        } catch (ClientProtocolException e) {
            mStringBuilder.append("客户网络协议错误，请重试！");
        } catch (IOException e) {
            mStringBuilder.append("IO传输错误，请重试！");
        } catch (JSONException e) {
            mStringBuilder.delete(0, mStringBuilder.length());
            mStringBuilder.append("没有找到相关翻译！\n");
        }

        return mStringBuilder.toString();
    }


    @Override
    protected void onPostExecute(String aString) {
        if (isCancelled()) return;
        mOnQueryComplete.forResult(RESULT_COMPLETE, aString);
    }
}
