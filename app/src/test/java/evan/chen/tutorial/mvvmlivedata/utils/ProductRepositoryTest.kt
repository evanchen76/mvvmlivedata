package evan.chen.tutorial.mvvmlivedata.utils

import evan.chen.tutorial.mvvmlivedata.IProductRepository
import evan.chen.tutorial.mvvmlivedata.ProductRepository
import evan.chen.tutorial.mvvmlivedata.api.IProductAPI
import evan.chen.tutorial.mvvmlivedata.api.ProductResponse
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class ProductRepositoryTest {
    private lateinit var repository: IProductRepository

    private var productResponse = ProductResponse()

    @MockK(relaxed = true)
    private lateinit var productAPI: IProductAPI

    @MockK(relaxed = true)
    private lateinit var repositoryCallback : IProductRepository.LoadProductCallback

    @Before
    fun setupPresenter() {
        MockKAnnotations.init(this)

        repository = ProductRepository(productAPI)

        productResponse.id = "pixel3"
        productResponse.name = "Google Pixel 3"
        productResponse.price = 27000
        productResponse.desc = "Desc"
    }

    @Test
    fun getProductTest() {

        //驗證跟Repository取得資料
        val productId = "pixel3"

        //驗證是否有呼叫IProductAPI.getProduct
        val slot = slot<IProductAPI.LoadAPICallBack>()

        every { productAPI.getProduct(any(), capture(slot)) }
            .answers {
                //將callback攔截下載並指定productResponse的值。
                slot.captured.onGetResult(productResponse)
            }

        repository.getProduct(productId, repositoryCallback)

        //驗證是否有呼叫Callback
        verify { repositoryCallback.onProductResult(productResponse) }
    }
}