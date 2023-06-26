package org.miui.refine.packageinstaller

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.miui.refine.R
import org.miui.refine.packageinstaller.PackageInstallerActivity.Companion.TAG
import org.miui.refine.packageinstaller.util.InstallUtil
import org.miui.refine.packageinstaller.util.PackageUtil
import org.miui.refine.ui.component.MIUIButton
import org.miui.refine.utils.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageInstallerScreen(
    navController: NavHostController, intentData: String
) {

    val viewModel = PackageInstallerViewModel()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            PackageInstallerTopBar()
        },
        containerColor = Color.White,
    ) {

        LaunchedEffect(key1 = intentData) {
            val intent = Intent.parseUri(intentData, 0)
            prepare(context = context, viewModel = viewModel, intent = intent)
        }

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
                if (viewModel.loading.value) {

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
                        painter = rememberDrawablePainter(drawable = viewModel.newAppInfo.value.icon),
                        contentDescription = "APP Icon",
                    )

                }


                Box(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center
                ) {

                    if (viewModel.loading.value) {

                        Box(
                            modifier = Modifier
                                .size(100.dp, 20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xfff0f0f0))
                        )

                    } else {

                        Text(
                            text = viewModel.newAppInfo.value.label.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )

                    }

                    Box(modifier = Modifier.height(5.dp))


                    if (viewModel.loading.value) {

                        Box(
                            modifier = Modifier
                                .size(200.dp, 20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xfff0f0f0))
                        )

                    } else {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "版本" + ": ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = viewModel.newAppInfo.value.versionName,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        Box(modifier = Modifier.height(3.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "大小" + ": ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )

                            Text(
                                text = viewModel!!.newAppInfo.value.size.toString() + "MB",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }


            // Button Field
            if (viewModel.loading.value) {
                // loading
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // loading for install
                    Loading()

                }

            } else if (viewModel.installResult.value) {
                // install successed
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 10.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MIUIButton(
                        text = "打开"
                    ) {
                        // open installed app
                        val intent =
                            context.packageManager.getLaunchIntentForPackage(viewModel.newAppInfo.value.packageName);
                        Log.d(
                            TAG,
                            "open installed app intent: $intent packageName : ${viewModel.newAppInfo.value.packageName}"
                        )
                        if (intent != null) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent)
                        }
                    }

                    Box(modifier = Modifier.height(20.dp))

                    MIUIButton(
                        text = "完成"
                    ) {
                        PackageInstallerActivity.instance.finishAndRemoveTask()
                    }

                }

            } else if (viewModel!!.installing.value) {

                // app is installing
                Loading()

            } else {
                PrepareBtnGroup(viewModel = viewModel)
            }

        }

        if (viewModel!!.needRequestPermission.value) {
            RequestPermissionDialog(onDismiss = {
                viewModel!!.needRequestPermission.value = false
            }, onConfirm = {
                viewModel!!.needRequestPermission.value = false
                Shell.cmd("su").submit()
            })
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageInstallerTopBar() {

    val context = LocalContext.current

    CenterAlignedTopAppBar(
        modifier = Modifier.padding(horizontal = 6.dp),
        navigationIcon = {
            IconButton(
                onClick = { PackageInstallerActivity.instance.finishAndRemoveTask() },
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
                onClick = {
                    Toast.makeText(
                        context, "comming soon", Toast.LENGTH_SHORT
                    ).show()
                },
            ) {
                Icon(
                    painterResource(R.drawable.ic_miui_setting),
                    contentDescription = "",
                    modifier = Modifier.size(27.dp)
                )
            }
        },
        title = {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_refine),
                    contentDescription = "refine",
                    modifier = Modifier
                        .height(20.dp)
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
}

@Composable
fun RequestPermissionDialog(
    onDismiss: () -> Unit, onConfirm: () -> Unit
) {
    AlertDialog(onDismissRequest = {
        onDismiss()
    }, title = {
        Text(text = "权限申请")
    }, text = {
        Text(text = "安装app需要root权限，是否授权？")
    }, confirmButton = {
        TextButton(onClick = {
            onConfirm()
        }) {
            Text("授权")
        }
    }, dismissButton = {
        TextButton(onClick = {
            onDismiss()
        }) {
            Text("取消")
        }
    })
}

@Composable
fun Loading() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            R.raw.loading
        )
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(150.dp)) {
            LottieAnimation(
                modifier = Modifier.matchParentSize(),
                alignment = Alignment.Center,
                composition = composition,
                iterations = LottieConstants.IterateForever
            )
        }
    }
}

@Composable
fun PrepareBtnGroup(viewModel: PackageInstallerViewModel) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 10.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        MIUIButton(text = "详细信息") {

        }

        Box(modifier = Modifier.height(10.dp))

        MIUIButton(text = "继续") {

            viewModel.installing.value = true

            InstallUtil.installPackage(mRealPackageUri = viewModel.mCachePackageFile!!,
                requestPermissionCallBack = {
                    viewModel.needRequestPermission.value = true
                    viewModel.installing.value = false
                },
                resultCallBack = { isSuccess ->
                    viewModel.installResult.value = isSuccess
                    viewModel.installing.value = false

                    Toast.makeText(
                        context, if (isSuccess) "安装完成" else "安装失败",

                        Toast.LENGTH_SHORT
                    ).show()
                })

        }

        Box(modifier = Modifier.height(10.dp))

        MIUIButton(text = "取消") {
            PackageInstallerActivity.instance.finishAndRemoveTask()
        }

    }
}

fun prepare(context: Context, viewModel: PackageInstallerViewModel, intent: Intent) {


    GlobalScope.launch {
        if (intent.scheme == ContentResolver.SCHEME_CONTENT) {

            viewModel.mRealPackageUri = intent.data!!

            viewModel.mCachePackageFile = InstallUtil.copyPackage2Cache(
                mContext = context, mRealPackageUri = viewModel.mRealPackageUri!!
            )

            if (viewModel.mCachePackageFile != null) {
                InstallUtil.processPackageUri(
                    mContext = context, packageUri = Uri.fromFile(viewModel.mCachePackageFile!!)
                ).also {
                    if (it != null) {
                        viewModel.newAppInfo.value = it
                        viewModel.loading.value = false
                    }
                    Log.d("InstallStart", "appSnippet: $it")
                }

                viewModel.oldAppInfo = PackageUtil.getInstalledPackageInfo(
                    pm = context.packageManager,
                    packageName = viewModel.newAppInfo.value.packageName
                )
            }
        } else {
            Toast.makeText(
                context, "Unsupported Uri Scheme", Toast.LENGTH_SHORT
            ).show()
        }
    }
}