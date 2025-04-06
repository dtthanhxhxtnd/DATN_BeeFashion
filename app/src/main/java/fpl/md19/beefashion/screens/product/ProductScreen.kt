package fpl.md19.beefashion.screens.product

import ProductDetailViewModel
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import fpl.md19.beefashion.R
import fpl.md19.beefashion.GlobalVarible.UserSesion
import fpl.md19.beefashion.components.AddToCartBottomSheet
import fpl.md19.beefashion.components.BuyNowBottomSheet
import fpl.md19.beefashion.screens.adress.AddressPreferenceManager
import fpl.md19.beefashion.screens.payment.formatCurrency
import fpl.md19.beefashion.viewModels.AddressViewModel
import fpl.md19.beefashion.viewModels.BrandViewModel
import fpl.md19.beefashion.viewModels.CartViewModel
import fpl.md19.beefashion.viewModels.FavoriteViewModel
import fpl.md19.beefashion.viewModels.LoginViewModel
import fpl.md19.beefashion.viewModels.ProductsViewModels
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductScreen(
    productId: String,
    isFavoriteByCurrentUser: Boolean,
    navController: NavController,
    favoriteViewModel: FavoriteViewModel = viewModel(),
    productDetailViewModel: ProductDetailViewModel = viewModel(),
    brandsViewModel: BrandViewModel = viewModel(),
    productsViewModels: ProductsViewModels = viewModel(),
    loginViewModel: LoginViewModel = viewModel(),
) {
    var selectedSize by remember { mutableStateOf("") }
    var selectedQuantity by remember { mutableStateOf(0) }
    var isFavorite by remember { mutableStateOf(isFavoriteByCurrentUser) }
    var showLoginDialog by remember { mutableStateOf(false) }
    val isLoggedIn = UserSesion.currentUser != null

    val context = LocalContext.current
    var showBottomSheet by remember { mutableStateOf(false) }
    var showAddBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        productDetailViewModel.fetchProductDetails(productId)
    }

    val product by productDetailViewModel.productDetail.observeAsState()
    val errorMessage by productDetailViewModel.errorMessage.observeAsState()

    val brands by brandsViewModel.brand
    val products by productsViewModels.products
    val sizes = product?.sizes ?: listOf()
    val quantities = product?.quantities ?: listOf()

    val addressPreferenceManager = remember { AddressPreferenceManager(context) }
    val addressViewModel: AddressViewModel = viewModel()
    val addresses by addressViewModel.addresses.collectAsState()

    val selectedAddress by remember { mutableStateOf(addressPreferenceManager.getSelectedAddress()) }

    val selectedAddressModel = addresses.find { it.id == selectedAddress }

    LaunchedEffect(product) {
        if (sizes.isNotEmpty() && quantities.isNotEmpty()) {
            selectedSize = sizes.first().name
            selectedQuantity = quantities.first()
        }
    }

    LaunchedEffect(Unit) {
        loginViewModel.loadRememberedCredentials(context) {

        }
    }

    if (showLoginDialog) {
        LoginDialog(
            onDismiss = { showLoginDialog = false },
            onLogin = { navController.navigate("loginScreen") }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
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
                text = "Chi tiết sản phẩm",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                painter = painterResource(id = R.drawable.bell),
                contentDescription = "Notifications",
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = Color.Red,
                fontSize = 16.sp,
                modifier = Modifier.padding(8.dp)
            )
        } else if (product != null) {
            val productBrand = brands.find { it.id == product!!.brandId }
            val selectedProduct = products.find { it.id == productId }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
            ) {
                AsyncImage(
                    model = selectedProduct?.image,
                    contentDescription = "Product Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(42.dp)
                        .border(0.1.dp, Color.Black, RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .clickable {
                            isFavorite = !isFavorite;
                            if (isFavorite) {
                                favoriteViewModel.addFavoriteProducts(productId)
                                Toast.makeText(context, "Đã thêm sản phẩm '${selectedProduct?.name ?: ""} vào mục yêu thích!", Toast.LENGTH_SHORT).show()
                            } else {
                                favoriteViewModel.removeFavoriteProducts(productId)
                                Toast.makeText(context, "Đã hủy sản phẩm '${selectedProduct?.name ?: ""} khỏi mục yêu thích!", Toast.LENGTH_SHORT).show()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.heart),
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.Gray,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                item {
                    Row(
                        modifier = Modifier.padding(top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedProduct?.name ?: "",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "(${productBrand?.name ?: "Không có thương hiệu"})",
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(start = 10.dp)
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.padding(top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.star),
                            contentDescription = "Rating",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = " 4.0/5 ",
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "(45 đánh giá)",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    Text(
                        text = "${product?.description}",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 15.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.padding(end = 25.dp)
                    ) {
                        Text(
                            text = "Giá",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = formatCurrency(product?.price),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = {
                            loginViewModel.loadRememberedCredentials(context) {
                                if (isLoggedIn) {
                                    showAddBottomSheet = true
                                } else {
                                    showLoginDialog = true
                                }
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.add_to_cart),
                            contentDescription = "Thêm vào giỏ hàng",
                            tint = Color.Black,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(3.dp))

                    Button(
                        onClick = {
                            if (selectedAddressModel == null) {
                                navController.navigate("LoginScreen")
                                Toast.makeText(context, "Vui lòng đăng nhập trước khi mua!", Toast.LENGTH_SHORT).show()
                            }
                            // Kiểm tra lại trạng thái đăng nhập trước khi thực hiện hành động mua hàng
//                            loginViewModel.loadRememberedCredentials(context) {
//                                // Callback khi đăng nhập tự động thành công (nếu có thông tin đăng nhập đã lưu)
//                                if (UserSesion.currentUser != null) {
//                                    showBottomSheet = true // Hiển thị bottom sheet nếu đăng nhập thành công
//                                } else {
//                                    showLoginDialog = true
//                                }
//                            }

                            // Kiểm tra trạng thái đăng nhập sau khi tải thông tin
                            // Người dùng đã đăng nhập, hiển thị bottom sheet mua hàng
                            loginViewModel.loadRememberedCredentials(context) {
                                if (isLoggedIn) {
                                    showBottomSheet = true
                                } else {
                                    showLoginDialog = true
                                }
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(Color.Red),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Mua ngay",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (showBottomSheet) {
                        selectedAddress?.let {
                            BuyNowBottomSheet(
                                productsViewModels = productsViewModels,
                                viewModel = productDetailViewModel,
                                productId = productId,
                                onDismiss = { showBottomSheet = false },
                                navController = navController,
                                selectedAddress = it,
                                addresses = addresses
                            )
                        }
                    }
                    if (showAddBottomSheet) {
                        AddToCartBottomSheet(
                            productsViewModels = productsViewModels,
                            productDetailViewModel = productDetailViewModel,
                            productId = productId,
                            onDismiss = { showAddBottomSheet = false },
                            navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoginDialog(
    onDismiss: () -> Unit,
    onLogin: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Thông báo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Bạn cần đăng nhập để tiếp tục mua hàng",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Hủy")
                    }

                    Button(
                        onClick = {
                            onDismiss()
                            onLogin()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Đăng nhập")
                    }
                }
            }
        }
    }
}

fun formatCurrency(price: Any?): String {
    val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
    return when (price) {
        is Number -> formatter.format(price.toLong()) + " ₫"
        is String -> price.toLongOrNull()?.let { formatter.format(it) + " ₫" } ?: "0 ₫"
        else -> "0 đ"
    }
}