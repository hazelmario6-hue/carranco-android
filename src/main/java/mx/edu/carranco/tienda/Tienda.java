package mx.edu.carranco.tienda;
import android.app.Activity;
import android.os.Bundle;
import android.net.Uri;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.graphics.Color;
import android.webkit.*;
import android.widget.*;
import android.view.*;

public class Tienda extends Activity {
 static final String HOST="carrancoecommerce-production.up.railway.app", HOME="https://"+HOST+"/";
 WebView web; LinearLayout panel; ProgressBar progress;
 boolean internal(Uri u){return "https".equalsIgnoreCase(u.getScheme()) && HOST.equalsIgnoreCase(u.getHost()) && (u.getPort()==-1 || u.getPort()==443);}
 void error(){web.setVisibility(View.GONE);panel.setVisibility(View.VISIBLE);progress.setVisibility(View.GONE);}
 public void onCreate(Bundle state){super.onCreate(state);
 FrameLayout root=new FrameLayout(this); setContentView(root);
 root.setOnApplyWindowInsetsListener((v,i)->{v.setPadding(i.getSystemWindowInsetLeft(),i.getSystemWindowInsetTop(),i.getSystemWindowInsetRight(),i.getSystemWindowInsetBottom());return i;});
 web=new WebView(this);root.addView(web);
 progress=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);root.addView(progress,new FrameLayout.LayoutParams(-1,8,Gravity.TOP));
 panel=new LinearLayout(this);panel.setBackgroundColor(Color.rgb(251,247,239));panel.setOrientation(LinearLayout.VERTICAL);panel.setGravity(Gravity.CENTER);root.addView(panel);
 TextView msg=new TextView(this);msg.setText("No pudimos abrir la tienda. Revisa tu conexión.");panel.addView(msg);
 Button retry=new Button(this);retry.setText("Volver a intentar");panel.addView(retry);retry.setOnClickListener(v->web.loadUrl(HOME));panel.setVisibility(View.GONE);
 web.getSettings().setJavaScriptEnabled(true); web.getSettings().setDomStorageEnabled(true);
 web.getSettings().setAllowFileAccess(false);web.getSettings().setAllowContentAccess(false);
 web.setWebChromeClient(new WebChromeClient(){public void onProgressChanged(WebView w,int p){progress.setProgress(p);progress.setVisibility(p==100?View.GONE:View.VISIBLE);}});
 web.setWebViewClient(new WebViewClient(){
 public boolean shouldOverrideUrlLoading(WebView w,WebResourceRequest r){Uri u=r.getUrl();if(internal(u))return false;
 if(r.isForMainFrame() && ("https".equalsIgnoreCase(u.getScheme()) || "mailto".equals(u.getScheme()) || "tel".equals(u.getScheme())))try{startActivity(new Intent(Intent.ACTION_VIEW,u));}catch(ActivityNotFoundException ex){Toast.makeText(Tienda.this,"No hay una aplicación para abrir el enlace.",Toast.LENGTH_SHORT).show();}return true;}
 public void onPageStarted(WebView w,String url,android.graphics.Bitmap icon){panel.setVisibility(View.GONE);web.setVisibility(View.VISIBLE);}
 public void onReceivedError(WebView w,WebResourceRequest r,WebResourceError e){if(r.isForMainFrame())error();}
 public void onReceivedHttpError(WebView w,WebResourceRequest r,WebResourceResponse e){if(r.isForMainFrame() && e.getStatusCode()>=500)error();}
 });
 if(state==null || web.restoreState(state)==null)web.loadUrl(HOME);
 }
 public void onSaveInstanceState(Bundle b){super.onSaveInstanceState(b);web.saveState(b);}
 public void onBackPressed(){if(web.canGoBack())web.goBack();else super.onBackPressed();}
}
