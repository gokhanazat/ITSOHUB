package com.mgacreative.globaltrade.core.pdf

import com.mgacreative.globaltrade.core.domain.showroom.ShowroomProduct
import com.mgacreative.globaltrade.core.domain.b2b.B2BCompany

expect object PdfGenerator {
    fun generateShowroomCatalog(
        products: List<ShowroomProduct>,
        company: B2BCompany? = null
    ): ByteArray
    fun generateProductDetail(product: ShowroomProduct): ByteArray
}
