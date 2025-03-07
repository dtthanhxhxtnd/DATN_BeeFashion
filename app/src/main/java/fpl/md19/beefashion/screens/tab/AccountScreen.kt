package fpl.md19.beefashion.screens.tab

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import fpl.md19.beefashion.GlobalVarible.UserSesion
import fpl.md19.beefashion.R
import fpl.md19.beefashion.components.LogOutComponent
import fpl.md19.beefashion.viewModels.LoginViewModel

@Composable
fun AccountScreen(navController: NavController, loginViewModel: LoginViewModel = viewModel()) {
    var showLogoutDialog = remember { mutableStateOf(false) }
    val isLoggedIn = UserSesion.currentUser != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp, top = 30.dp, end = 25.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { navController.popBackStack() }
            )
            Text(
                text = "Tài khoản",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                painter = painterResource(id = R.drawable.bell),
                contentDescription = "Notifications",
                modifier = Modifier.size(24.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 12.dp)
        ) {
            if (isLoggedIn) {
                item { Divider() }
                item { AccountItem(R.drawable.ic_orders, "Đơn hàng", navController, "MyOrderScreen") }
                item { Divider(thickness = 8.dp, color = Color.LightGray) }
                item { AccountItem(R.drawable.ic_details, "Thông tin", navController, "MyDetailsScreen") }
                item { AccountItem(R.drawable.ic_address, "Địa chỉ", navController, "AddressScreen/{customerId}") }
                item { AccountItem(R.drawable.ic_notifications, "Thông báo", navController, "NotificationsScreen") }
                item { Divider(thickness = 8.dp, color = Color.LightGray) }
                item { AccountItem(R.drawable.ic_help, "Trợ giúp", navController, "HelpScreen") }
                item { Divider(thickness = 8.dp, color = Color.LightGray) }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { LogoutItem(onClick = { showLogoutDialog.value = true }) }
            } else {
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { LoginItem(navController) }
            }
        }
    }

    if (isLoggedIn) {
        LogOutComponent(
            onConfirm = {
                loginViewModel.logout(context = navController.context)
                UserSesion.currentUser = null
                showLogoutDialog.value = false
                navController.navigate("LoginScreen") {
                    popUpTo("HomeScreen") { inclusive = true }
                }
            },
            onDismiss = { showLogoutDialog.value = false },
            isVisible = showLogoutDialog.value
        )
    }
}

@Composable
fun AccountItem(imageRes: Int, title: String, navController: NavController, route: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(route) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontSize = 16.sp)
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = "Arrow",
            modifier = Modifier.size(24.dp)
        )
    }
    Divider()
}

@Composable
fun LogoutItem(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logout),
            contentDescription = "Logout",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "Đăng xuất", color = Color.Red, fontSize = 16.sp)
    }
}

@Composable
fun LoginItem(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("LoginScreen") }
                .background(color = Color(0xFF3498DB), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.login), // Bạn cần thêm icon đăng nhập
                contentDescription = "Login",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Đăng nhập",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewAccountScreen() {
    val navController = rememberNavController()
    AccountScreen(navController)
}
