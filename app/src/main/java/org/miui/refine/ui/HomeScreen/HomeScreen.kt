package org.miui.refine.ui.HomeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.topjohnwu.superuser.Shell
import org.miui.refine.R
import org.miui.refine.model.RemoteSharePreferences
import org.miui.refine.ui.component.MIUISwitchColors
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {

    val viewModel: HomeViewModel = remember { HomeViewModel() }

    loadStatus(viewModel)
    Scaffold(contentWindowInsets = WindowInsets(0.dp), topBar = {
        CenterAlignedTopAppBar(colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            //containerColor = Color.Yellow,
        ), title = {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_refine),
                    contentDescription = "refine",
                    modifier = Modifier
                        .height(15.dp)
                        .wrapContentWidth(),
                    contentScale = ContentScale.FillHeight,
                    colorFilter = ColorFilter.tint(Color.Black),
                )
            }

        })
    }) { contentPadding ->


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .fillMaxSize()
                    .verticalScroll(
                        enabled = true,
                        state = rememberScrollState(0)
                    )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "授予ROOT", fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                    Switch(
                        modifier = Modifier.semantics {
                            contentDescription = "Demo"
                        },
                        checked = viewModel.isGrantedRoot.value,
                        enabled = false,
                        onCheckedChange = { viewModel.isGrantedRoot.value = it },
                        colors = MIUISwitchColors()
                    )
                }

                Box(modifier = Modifier.height(15.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "启用XPosed",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                    Switch(
                        modifier = Modifier.semantics {
                            contentDescription = "Demo"
                        },
                        enabled = false,
                        checked = viewModel.isXposedActive.value,
                        onCheckedChange = { viewModel.isGrantedRoot.value = it },
                        colors = MIUISwitchColors()
                    )
                }

                Divider(
                    modifier = Modifier.padding(vertical = 20.dp),
                    thickness = 1.dp,
                    color = Color(0xFFE4E4E4)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "接管系统安装器",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        )
                        Text(
                            text = "将系统安装器替换为REFINE", fontSize = 14.sp, color = Color.Gray
                        )
                    }

                    Switch(
                        modifier = Modifier.semantics {
                            contentDescription = "Demo"
                        },
                        checked = viewModel.replacePackageInstaller.value,
                        onCheckedChange = { viewModel.replacePackageInstaller.value = it },
                        colors = MIUISwitchColors()
                    )
                }

                Divider(
                    modifier = Modifier.padding(vertical = 20.dp),
                    thickness = 1.dp,
                    color = Color(0xFFE4E4E4)
                )
                Text(
                    text = "调试",
                    color = Color.Gray,
                    fontSize = 14.sp,
                )

                Box(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "允许 USB 安装",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        )
                        Text(
                            text = "无需安装手机卡", fontSize = 14.sp, color = Color.Gray
                        )
                    }
                    Switch(modifier = Modifier.semantics {
                        contentDescription = "Demo"
                    }, checked = viewModel.enableUSBinstall.value, onCheckedChange = {

                        viewModel.CenterSharePreferences!!.putBoolean(
                            "security_adb_install_enable", it
                        )

                        viewModel.enableUSBinstall.value = it
                    }, colors = MIUISwitchColors()
                    )
                }


                Box(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {
                        Text(
                            text = "禁用 USB 安装验证",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        )
                        Text(
                            text = "禁用二次弹窗验证", fontSize = 14.sp, color = Color.Gray
                        )
                    }
                    Switch(modifier = Modifier.semantics {
                        contentDescription = "Demo"
                    }, checked = viewModel.verifyUSBinstall.value, onCheckedChange = {

                        viewModel.CenterSharePreferences!!.putBoolean(
                            "permcenter_install_intercept_enabled", !it
                        )

                        viewModel.verifyUSBinstall.value = it
                    }, colors = MIUISwitchColors()
                    )
                }
            }
        }
    }
}

fun loadStatus(viewModel: HomeViewModel) {

    viewModel.CenterSharePreferences = RemoteSharePreferences(
        "com.miui.securitycenter",
        "/data/data/com.miui.securitycenter/shared_prefs/remote_provider_preferences.xml"
    )
    val adb_install =
        viewModel.CenterSharePreferences!!.getBoolean("security_adb_install_enable", false)

    Timber.d("security_adb_install_enable  : $adb_install")

    viewModel.enableUSBinstall.value = adb_install

    val install =
        viewModel.CenterSharePreferences!!.getBoolean("permcenter_install_intercept_enabled", true)

    viewModel.verifyUSBinstall.value = !install

    viewModel.isGrantedRoot.value = Shell.isAppGrantedRoot() == true
}
