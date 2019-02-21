package my.com.engpeng.epslaughterhouse.model

data class TempSlaughterDetailTtl(
        val ttlWeight: Double,
        val ttlQty: Int,
        val ttlCage: Int,
        val ttlCover: Int)

data class TempSlaughterMortalityTtl(
        val ttlWeight: Double,
        val ttlQty: Int)