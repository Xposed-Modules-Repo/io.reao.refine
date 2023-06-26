package org.miui.redesign.packageinstaller


import android.content.ContentResolver
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.miui.redesign.R
import org.miui.redesign.packageinstaller.model.AppInfo
import org.miui.redesign.packageinstaller.util.InstallUtil
import org.miui.redesign.packageinstaller.util.PackageUtil
import org.miui.redesign.ui.theme.RedesignTheme
import org.miui.redesign.utils.Log
import java.io.File
import java.io.FileOutputStream


class InstallPackageActivity : ComponentActivity() {

    companion object {
        val SCHEME_PACKAGE = "package"
        val TAG = InstallPackageActivity::class.java.simpleName
    }

    private var mRealPackageUri: Uri? = null
    private var mCachePackageUri: File? = null

    private var viewModel: InstallMainViewModel? = null

    @OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = InstallMainViewModel()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {

            ViewSetup()

            RedesignTheme {
                ProvideWindowInsets {
                    Scaffold(
                        topBar = {
                            CenterAlignedTopAppBar(
                                modifier = Modifier.padding(horizontal = 6.dp),
                                navigationIcon = {
                                    IconButton(
                                        onClick = { this@InstallPackageActivity.finish() },
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.ArrowBack,
                                            contentDescription = "Localized description",
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(
                                        onClick = { },
                                    ) {
                                        Icon(
                                            painterResource(R.drawable.ic_miui_setting),
                                            contentDescription = "",
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                },
                                title = {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_redesign),
                                            contentDescription = "redesign",
                                            modifier = Modifier
                                                .height(15.dp)
                                                .wrapContentWidth(),
                                            contentScale = ContentScale.FillHeight,
                                            colorFilter = ColorFilter.tint(Color.LightGray),
                                        )
                                    }

                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = Color.Transparent
                                ),
                            )
                        },
                        containerColor = Color.White,
                    ) {

                        Column(
                            modifier = Modifier
                                .padding(it)
                                .padding(vertical = 20.dp, horizontal = 20.dp)
                        ) {

                            Row(
                                Modifier
                                    .height(80.dp)
                                    .fillMaxWidth(),
                            ) {
                                if (viewModel!!.loading.value) {

                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(Color(0xfff0f0f0))
                                    )

                                } else {

                                    Image(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(20.dp)),
                                        painter = rememberDrawablePainter(drawable = viewModel!!.appInfo.value.icon),
                                        contentDescription = "APP Icon",
                                    )

                                }


                                Box(modifier = Modifier.width(10.dp))

                                Column(
                                    modifier = Modifier.fillMaxHeight(),
                                    verticalArrangement = Arrangement.Center
                                ) {

                                    if (viewModel!!.loading.value) {

                                        Box(
                                            modifier = Modifier
                                                .size(100.dp, 20.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xfff0f0f0))
                                        )

                                    } else {

                                        Text(
                                            text = viewModel!!.appInfo.value.label.toString(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp
                                        )

                                    }

                                    Box(modifier = Modifier.height(5.dp))


                                    if (viewModel!!.loading.value) {

                                        Box(
                                            modifier = Modifier
                                                .size(200.dp, 20.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xfff0f0f0))
                                        )

                                    } else {

                                        Text(
                                            text = "版本：" + viewModel!!.appInfo.value.versionName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        )

                                    }
                                }
                            }

                            if (viewModel!!.loading.value) {

                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = "正在加载，请稍后")
                                }

                            } else {

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(bottom = 10.dp),
                                    verticalArrangement = Arrangement.Bottom,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Button(
                                        modifier = Modifier.fillMaxWidth(), onClick = {

                                            InstallUtil.installPackage(
                                                mRealPackageUri = mCachePackageUri!!,
                                                requestPermissionCallBack = {
                                                    viewModel!!.needRequestPermission.value = true
                                                }, resultCallBack = { isSuccess ->
                                                    viewModel!!.installResult.value = isSuccess

                                                    Toast.makeText(
                                                        this@InstallPackageActivity,
                                                        if (isSuccess) "安装完成" else "安装失败",

                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                })
                                        }, colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xfff0f0f0),
                                            contentColor = Color.Black
                                        )
                                    ) {
                                        Text(
                                            modifier = Modifier.padding(vertical = 5.dp),
                                            text = "继续",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Box(modifier = Modifier.height(20.dp))

                                    Button(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = { this@InstallPackageActivity.finish() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xfff0f0f0),
                                            contentColor = Color.Black
                                        )
                                    ) {
                                        Text(
                                            modifier = Modifier.padding(vertical = 5.dp),
                                            text = "取消",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                        }

                        if (viewModel!!.needRequestPermission.value) {
                            AlertDialog(onDismissRequest = {
                                // Dismiss the dialog when the user clicks outside the dialog or on the back
                                // button. If you want to disable that functionality, simply use an empty
                                // onDismissRequest.
                                viewModel!!.needRequestPermission.value == false
                            }, title = {
                                Text(text = "权限申请")
                            }, text = {
                                Text(text = "安装app需要root权限，是否授权？")
                            }, confirmButton = {
                                TextButton(onClick = {
                                    viewModel!!.needRequestPermission.value = false
                                    Shell.cmd("su").submit()
                                }) {
                                    Text("授权")
                                }
                            }, dismissButton = {
                                TextButton(onClick = {
                                    viewModel!!.needRequestPermission.value = false
                                }) {
                                    Text("取消")
                                }
                            })
                        }

                    }
                }
            }
        }

        GlobalScope.launch {
            if (intent.scheme == ContentResolver.SCHEME_CONTENT) {

                mRealPackageUri = intent.data!!


                mCachePackageUri = InstallUtil.copyFile2Cache(
                    mContext = this@InstallPackageActivity,
                    mRealPackageUri = mRealPackageUri!!
                )

                if (mCachePackageUri != null) {
                    InstallUtil.processPackageUri(
                        mContext = this@InstallPackageActivity,
                        packageUri = Uri.fromFile(mCachePackageUri!!)
                    ).also {
                        if (it != null) {
                            viewModel!!.appInfo.value = it
                            viewModel!!.loading.value = false
                        }
                        Log.d("InstallStart", "appSnippet: $it")
                    }
                }

            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
    }


    @Composable
    fun ViewSetup() {
        val systemUiController = rememberSystemUiController()

        SideEffect {
            systemUiController.setSystemBarsColor(
                color = Color.White, darkIcons = true
            )
        }
    }

}