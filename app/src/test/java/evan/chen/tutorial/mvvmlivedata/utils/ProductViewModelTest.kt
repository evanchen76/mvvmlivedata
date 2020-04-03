package evan.chen.tutorial.mvvmlivedata.utils

import android.arch.core.executor.testing.InstantTaskExecutorRule
import evan.chen.tutorial.mvvmlivedata.IProductRepository
import evan.chen.tutorial.mvvmlivedata.ProductViewModel
import evan.chen.tutorial.mvvmlivedata.api.ProductResponse
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProductViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @MockK(relaxed = true)
    lateinit var repository: IProductRepository
    private var productResponse = ProductResponse()
    private lateinit var viewModel: ProductViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        productResponse.id = "pixel3"
        productResponse.name = "Google Pixel 3"
        productResponse.price = 27000
        productResponse.desc = "Desc"

        viewModel = ProductViewModel(repository)
    }

    @Test
    fun getProductTest() {
        val productId = "pixel3"

        val slot = slot<IProductRepository.LoadProductCallback>()

        //驗證是否有呼叫IProductRepository.getProduct
        every { repository.getProduct(eq(productId), capture(slot)) }
            .answers {
                //將callback攔截下載並指定productResponse的值。
                slot.captured.onProductResult(productResponse)
            }

        viewModel.getProduct(productId)

        Assert.assertEquals(productResponse.name, viewModel.productName.value)
        Assert.assertEquals(productResponse.desc, viewModel.productDesc.value)
        Assert.assertEquals(productResponse.price, viewModel.productPrice.value)
    }

    @Test
    fun buySuccess() {

        val productId = "pixel3"
        val items = 3
        val productViewModel = ProductViewModel(repository)
        productViewModel.productId.value =  productId
        productViewModel.productItems.value = items.toString()


        val slot = slot<IProductRepository.BuyProductCallback>()

        every { repository.buy(eq(productId), eq(items), capture(slot)) }
            .answers {
                slot.captured.onBuyResult(true)
            }

        productViewModel.buy()

        Assert.assertTrue(productViewModel.buySuccessText.value != null)
    }

    @Test
    fun buyFail() {

        val productId = "pixel3"
        val items = 11
        val productViewModel = ProductViewModel(repository)
        productViewModel.productId.value = productId
        productViewModel.productItems.value = items.toString()

        val slot = slot<IProductRepository.BuyProductCallback>()

        every { repository.buy(eq(productId), eq(items), capture(slot)) }
            .answers {
                slot.captured.onBuyResult(false)
            }

        productViewModel.buy()

        Assert.assertTrue(productViewModel.alertText.value != null)
    }
}