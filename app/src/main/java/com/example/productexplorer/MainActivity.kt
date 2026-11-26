package com.example.productexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.productexplorer.ui.theme.ProductExplorerTheme
import java.util.Locale

data class ProductUi(
    val id: Int,
    val title: String,
    val brand: String,
    val category: String,
    val description: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val warrantyInformation: String,
    val shippingInformation: String,
    @DrawableRes val imageRes: Int
)

fun sampleProduct(): ProductUi {
    return ProductUi(
        id = 1,
        title = "Smartphone Toto X",
        brand = "TotoTech",
        category = "Smartphones",
        description = "Un smartphone léger avec un écran lumineux, une bonne autonomie et un design moderne.",
        price = 699.99,
        discountPercentage = 12.5,
        rating = 4.6,
        stock = 34,
        warrantyInformation = "Garantie constructeur : 2 ans",
        shippingInformation = "Livraison estimée : 3 à 5 jours ouvrés",
        imageRes = R.drawable.smartphone
    )
}

fun sampleProductOutOfStock(): ProductUi {
    return ProductUi(
        id = 2,
        title = "Casque Audio Pulse",
        brand = "SoundPeak",
        category = "Audio",
        description = "Un casque confortable conçu pour écouter de la musique, suivre des cours en ligne et travailler dans de bonnes conditions.",
        price = 129.99,
        discountPercentage = 8.0,
        rating = 4.2,
        stock = 0,
        warrantyInformation = "Garantie constructeur : 1 an",
        shippingInformation = "Produit temporairement indisponible",
        imageRes = R.drawable.casque
    )
}

fun sampleProducts(): List<ProductUi> {
    return listOf(
        sampleProduct(),
        sampleProductOutOfStock(),
        ProductUi(
            id = 3,
            title = "Montre Connectée FitTime",
            brand = "FitTime",
            category = "Wearables",
            description = "Une montre connectée simple pour suivre l'activité quotidienne.",
            price = 89.99,
            discountPercentage = 15.0,
            rating = 4.4,
            stock = 18,
            warrantyInformation = "Garantie constructeur : 2 ans",
            shippingInformation = "Livraison estimée : 2 à 4 jours ouvrés",
            imageRes = R.drawable.montre
        ),
        ProductUi(
            id = 4,
            title = "Enceinte Mini Boom",
            brand = "BoomSound",
            category = "Audio",
            description = "Une enceinte compacte pour écouter de la musique à la maison ou en déplacement.",
            price = 59.99,
            discountPercentage = 5.0,
            rating = 4.1,
            stock = 52,
            warrantyInformation = "Garantie constructeur : 1 an",
            shippingInformation = "Livraison estimée : 3 jours ouvrés",
            imageRes = R.drawable.enceinte
        )
    )
}

fun sampleCategories(): List<String> {
    return listOf(
        "Smartphones",
        "Audio",
        "Wearables",
        "Maison"
    )
}

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val systemDarkTheme = isSystemInDarkTheme()

            var darkTheme by rememberSaveable {
                mutableStateOf(systemDarkTheme)
            }

            ProductExplorerTheme(darkTheme = darkTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text("Product Explorer")
                            },
                            actions = {
                                IconButton(
                                    onClick = {
                                        darkTheme = !darkTheme
                                    }
                                ) {
                                    Text(
                                        text = if (darkTheme) "☀️" else "🌙",
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    ProductCatalogContainer(
                        products = sampleProducts(),
                        categories = sampleCategories(),
                        onProductClick = {},
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ProductCatalogContainer(
    products: List<ProductUi>,
    categories: List<String>,
    onProductClick: (ProductUi) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }

    var showOnlyInStock by rememberSaveable {
        mutableStateOf(false)
    }

    var favoriteProductIds by rememberSaveable {
        mutableStateOf(listOf<Int>())
    }

    var selectedProductId by rememberSaveable {
        mutableStateOf<Int?>(null)
    }

    val selectedProduct = products.find {
        it.id == selectedProductId
    }

    val filteredProducts = remember(
        products,
        searchQuery,
        showOnlyInStock
    ) {
        products.filter { product ->
            val matchesSearch =
                product.title.contains(searchQuery, ignoreCase = true) ||
                        product.brand.contains(searchQuery, ignoreCase = true) ||
                        product.category.contains(searchQuery, ignoreCase = true)

            val matchesStock =
                !showOnlyInStock || product.stock > 0

            matchesSearch && matchesStock
        }
    }

    fun toggleFavorite(productId: Int) {
        favoriteProductIds = if (favoriteProductIds.contains(productId)) {
            favoriteProductIds - productId
        } else {
            favoriteProductIds + productId
        }
    }

    BackHandler(enabled = selectedProduct != null) {
        selectedProductId = null
    }

    if (selectedProduct == null) {
        ProductCatalogScreen(
            products = filteredProducts,
            categories = categories,
            searchQuery = searchQuery,
            onSearchQueryChange = { newValue ->
                searchQuery = newValue
            },
            showOnlyInStock = showOnlyInStock,
            onToggleStockFilter = {
                showOnlyInStock = !showOnlyInStock
            },
            favoriteProductIds = favoriteProductIds,
            onFavoriteClick = { productId ->
                toggleFavorite(productId)
            },
            onResetFilters = {
                searchQuery = ""
                showOnlyInStock = false
            },
            onProductClick = { product ->
                selectedProductId = product.id
                onProductClick(product)
            },
            modifier = modifier
        )
    } else {
        ProductDetailContainer(
            product = selectedProduct,
            onBackClick = {
                selectedProductId = null
            },
            onAddToCartClick = {},
            modifier = modifier
        )
    }
}

@Composable
fun ProductCatalogScreen(
    products: List<ProductUi>,
    categories: List<String>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    showOnlyInStock: Boolean,
    onToggleStockFilter: () -> Unit,
    favoriteProductIds: List<Int>,
    onFavoriteClick: (Int) -> Unit,
    onResetFilters: () -> Unit,
    onProductClick: (ProductUi) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Catalogue produits",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Text(
                text = "Explorez une sélection de produits disponibles localement.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = {
                    Text(text = "Rechercher un produit")
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Button(
                onClick = onToggleStockFilter
            ) {
                Text(
                    text = if (showOnlyInStock) {
                        "Afficher tous les produits"
                    } else {
                        "Afficher uniquement les produits en stock"
                    }
                )
            }
        }

        item {
            CategoryRow(categories = categories)
        }

        item {
            Text(
                text = "${products.size} produit(s) affiché(s)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (products.isEmpty()) {
            item {
                EmptyProductsMessage(
                    onResetClick = onResetFilters
                )
            }
        } else {
            items(
                items = products,
                key = { product -> product.id }
            ) { product ->
                ProductListItem(
                    product = product,
                    isFavorite = favoriteProductIds.contains(product.id),
                    onFavoriteClick = {
                        onFavoriteClick(product.id)
                    },
                    onClick = {
                        onProductClick(product)
                    }
                )
            }
        }
    }
}

@Composable
fun ProductDetailContainer(
    product: ProductUi,
    onBackClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour au catalogue"
                )
            }

            Text(
                text = "Retour au catalogue",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        ProductDetailScreen(
            product = product,
            onAddToCartClick = onAddToCartClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun EmptyProductsMessage(
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Aucun produit trouvé",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Essayez une autre recherche ou modifiez vos filtres.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onResetClick) {
            Text("Réinitialiser les filtres")
        }
    }
}

@Composable
fun ProductListItem(
    product: ProductUi,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {

            ProductImage(
                productTitle = product.title,
                imageRes = product.imageRes,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = product.title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = product.brand,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${product.price} € • ★ ${product.rating}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onFavoriteClick) {
                Text(
                    text = if (isFavorite) {
                        "Retirer des favoris"
                    } else {
                        "Ajouter aux favoris"
                    }
                )
            }

            Button(onClick = onClick) {
                Text(text = "Voir le produit")
            }
        }
    }
}

@Composable
fun CategoryRow(
    categories: List<String>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            CategoryChip(label = category)
        }
    }
}

@Composable
fun ProductHomeScreen(
    featuredProduct: ProductUi,
    onFeaturedProductClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        HomeHeader()

        Spacer(modifier = Modifier.height(24.dp))

        SearchPreviewBar()

        Spacer(modifier = Modifier.height(24.dp))

        FeaturedProductSection(
            product = featuredProduct,
            onClick = onFeaturedProductClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        CategoriesSection()

        Spacer(modifier = Modifier.height(24.dp))

        DailyOfferBox()

        Spacer(modifier = Modifier.height(24.dp))

        PromotionsSection(
            products = sampleProducts()
        )
    }
}

@Composable
fun HomeHeader(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Product Explorer",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Découvrez les produits du moment",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun SearchPreviewBar(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp
    ) {
        Text(
            text = "Rechercher un produit...",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun FeaturedProductSection(
    product: ProductUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Produit mis en avant",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProductImage(
                productTitle = product.title,
                imageRes = product.imageRes,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = product.title,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = product.brand,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProductQuickInfoRow(
                price = product.price,
                rating = product.rating,
                stock = product.stock
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = onClick) {
                Text(text = "Voir le produit")
            }
        }
    }
}

@Composable
fun ProductQuickInfoRow(
    price: Double,
    rating: Double,
    stock: Int,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "$price €",
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "★ $rating",
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "$stock en stock",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CategoryChip(
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 8.dp
            ),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun CategoriesSection(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Catégories",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            CategoryChip(label = "Smartphones")

            Spacer(modifier = Modifier.weight(1f))

            CategoryChip(label = "Audio")

            Spacer(modifier = Modifier.weight(1f))

            CategoryChip(label = "Maison")
        }
    }
}

@Composable
fun DailyOfferBox(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Offre du jour",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Jusqu'à -20 % sur une sélection de produits",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
            shape = MaterialTheme.shapes.small,
            tonalElevation = 6.dp
        ) {
            Text(
                text = "Nouveau",
                modifier = Modifier.padding(
                    horizontal = 8.dp,
                    vertical = 4.dp
                ),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
fun ProductDetailScreen(
    product: ProductUi,
    onAddToCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        ProductImage(
            productTitle = product.title,
            imageRes = product.imageRes,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(bottom = 16.dp)
        )

        ProductHeader(
            title = product.title,
            brand = product.brand,
            category = product.category
        )

        ProductPriceCard(
            price = product.price,
            discountPercentage = product.discountPercentage,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        ProductRating(
            rating = product.rating,
            modifier = Modifier.padding(top = 16.dp)
        )

        ProductAvailabilityCard(
            stock = product.stock,
            shippingInformation = product.shippingInformation,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        ProductDescription(
            description = product.description,
            modifier = Modifier.padding(top = 16.dp)
        )

        ProductWarrantyField(
            warrantyInformation = product.warrantyInformation,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        AddToCartButton(
            onClick = onAddToCartClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
    }
}

@Composable
fun ProductHeader(
    title: String,
    brand: String,
    category: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = brand,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = category,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ProductPriceCard(
    price: Double,
    discountPercentage: Double,
    modifier: Modifier = Modifier
) {
    val savings = price * discountPercentage / 100
    val discountedPrice = price - savings

    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {

            if (discountPercentage > 0) {

                Text(
                    text = String.format(
                        Locale.FRANCE,
                        "%.2f €",
                        price
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    textDecoration = TextDecoration.LineThrough,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = String.format(
                        Locale.FRANCE,
                        "%.2f €",
                        discountedPrice
                    ),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Remise : $discountPercentage %",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = String.format(
                        Locale.FRANCE,
                        "Vous économisez %.2f €",
                        savings
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )

            } else {

                Text(
                    text = String.format(
                        Locale.FRANCE,
                        "%.2f €",
                        price
                    ),
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Aucune remise",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ProductImage(
    productTitle: String,
    @DrawableRes imageRes: Int,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = "Image du produit $productTitle",
        contentScale = ContentScale.Fit,
        modifier = modifier
    )
}

@Composable
fun ProductRating(
    rating: Double,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {

        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Note du produit"
        )

        Text(
            text = "$rating / 5",
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun ProductAvailabilityCard(
    stock: Int,
    shippingInformation: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Disponibilité",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Stock : $stock unités",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = shippingInformation,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ProductDescription(
    description: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Description",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun ProductWarrantyField(
    warrantyInformation: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = warrantyInformation,
        onValueChange = {},
        readOnly = true,
        label = {
            Text(text = "Garantie")
        },
        modifier = modifier
    )
}

@Composable
fun AddToCartButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(text = "Ajouter au panier")
    }
}

@Composable
fun PromotionProductCard(
    product: ProductUi,
    modifier: Modifier = Modifier
) {
    val discountedPrice =
        product.price * (1 - product.discountPercentage / 100)

    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            ProductImage(
                productTitle = product.title,
                imageRes = product.imageRes,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "-${product.discountPercentage} %",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = product.brand,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = String.format(
                    Locale.FRANCE,
                    "%.2f €",
                    product.price
                ),
                textDecoration = TextDecoration.LineThrough,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = String.format(
                    Locale.FRANCE,
                    "%.2f €",
                    discountedPrice
                ),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun PromotionsSection(
    products: List<ProductUi>,
    modifier: Modifier = Modifier
) {
    val promotedProducts = products
        .filter { it.discountPercentage > 0 }
        .sortedByDescending { it.discountPercentage }
        .take(2)

    Column(modifier = modifier.fillMaxWidth()) {

        Text(
            text = "Promotions du moment",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            promotedProducts.forEach { product ->
                PromotionProductCard(
                    product = product,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Thème clair")
@Composable
fun ProductCatalogScreenLightPreview() {
    ProductExplorerTheme(darkTheme = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ProductCatalogContainer(
                products = sampleProducts(),
                categories = sampleCategories(),
                onProductClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Thème sombre")
@Composable
fun ProductCatalogScreenDarkPreview() {
    ProductExplorerTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ProductCatalogContainer(
                products = sampleProducts(),
                categories = sampleCategories(),
                onProductClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Accueil")
@Composable
fun ProductHomeScreenPreview() {
    ProductExplorerTheme {
        ProductHomeScreen(
            featuredProduct = sampleProduct(),
            onFeaturedProductClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Produit en stock")
@Composable
fun ProductDetailScreenPreview() {
    ProductExplorerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ProductDetailScreen(
                product = sampleProduct(),
                onAddToCartClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Produit en rupture")
@Composable
fun ProductDetailScreenOutOfStockPreview() {
    ProductExplorerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ProductDetailScreen(
                product = sampleProductOutOfStock(),
                onAddToCartClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Détail avec retour")
@Composable
fun ProductDetailContainerPreview() {
    ProductExplorerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ProductDetailContainer(
                product = sampleProduct(),
                onBackClick = {},
                onAddToCartClick = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Changement de thème")
@Composable
fun ThemeSwitchPreview() {
    var darkTheme by rememberSaveable {
        mutableStateOf(false)
    }

    ProductExplorerTheme(darkTheme = darkTheme) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text("Product Explorer")
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                darkTheme = !darkTheme
                            }
                        ) {
                            Text(
                                text = if (darkTheme) "☀️" else "🌙",
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            ProductCatalogContainer(
                products = sampleProducts(),
                categories = sampleCategories(),
                onProductClick = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}