package fpl.md19.beefashion

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import fpl.md19.beefashion.screens.adress.CancelOrderBottomSheet
import fpl.md19.beefashion.screens.adress.NotificationUtils
import fpl.md19.beefashion.screens.adress.OrderStatusStep
import fpl.md19.beefashion.screens.data.orderStatusList
import fpl.md19.beefashion.viewModels.InvoiceViewModel

//@Composable
//fun TrackOrderScreen(navController: NavController) {
//    Box(modifier = Modifier.fillMaxSize()) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color.White)
//                .padding(15.dp)
//        ) {
//            // Header
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 8.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.ic_back),
//                    contentDescription = "Back Icon",
//                    modifier = Modifier
//                        .size(20.dp)
//                        .clickable { navController.popBackStack() }
//                )
//                Text(
//                    text = "Tình trạng đơn hàng",
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Bold,
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier.weight(1f)
//                )
//                Image(
//                    painter = painterResource(id = R.drawable.ic_notifications),
//                    contentDescription = "Notification Icon",
//                    modifier = Modifier
//                        .size(20.dp)
//                        .clickable { }
//                )
//            }
//
////            Box(
////                modifier = Modifier
////                    .fillMaxWidth()
////                    .height(200.dp)
////            ) {
////                Image(
////                    painter = painterResource(id = R.drawable.map_placeholder),
////                    contentDescription = "Map",
////                    modifier = Modifier.fillMaxSize(),
////                    contentScale = ContentScale.Crop
////                )
////            }
//        }
//
////        BottomSheetOrderStatus(
////            modifier = Modifier.align(Alignment.BottomCenter),
////            currentStatus = "Đã lấy hàng"
////        )
//    }
//}

@Composable
fun BottomSheetOrderStatus(
    navController: NavController,
    modifier: Modifier = Modifier,
    currentStatus: String
) {
    val cancellableStatuses = listOf("pending", "packing")
    val receivableStatuses = listOf("intransit")
    var showCancelDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val invoiceViewModel: InvoiceViewModel = viewModel()

    Surface(
        modifier = modifier
            .fillMaxWidth()
//            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(24.dp))
            .defaultMinSize(minHeight = 300.dp),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Text("Trạng thái", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            OrderStatusStep(currentStatus)

            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 1.dp,
                color = Color(0xFFEEEEEE)
            )

            when {
                currentStatus in receivableStatuses -> {
                    Button(
                        onClick = {
                            NotificationUtils.showOrderSuccessNotification(
                                context = context,
                                message = "Bạn đã nhận hàng thành công!"
                            )
                            invoiceViewModel.completeCustomerInvoice(UserSesion.selectedOrder!!.id!!)
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text(
                            text = "Đã nhận hàng",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                currentStatus in cancellableStatuses -> {
                    Button(
                        onClick = {
                            showCancelDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text(text = "Hủy", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                // Nếu muốn có trạng thái mặc định không hiện nút nào thì bỏ else, hoặc có thể:
                else -> {
                    // Không hiện gì
                }
            }

        }
    }

    if (showCancelDialog) {
        CancelOrderBottomSheet(
            onDismiss = { showCancelDialog = false },
            onCancel = { navController.popBackStack() },
            invoiceViewModel = invoiceViewModel
        )
    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewTrackOder () {
//    val navController = rememberNavController()
//    TrackOrderScreen(navController)
//}