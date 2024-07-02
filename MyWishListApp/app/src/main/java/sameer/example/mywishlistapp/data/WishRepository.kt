package sameer.example.mywishlistapp.data

import kotlinx.coroutines.flow.Flow


class WishRepository(private val wishDao: WishDao) {

    suspend fun addAWish(wish: Wish){
        wishDao.addAWish(wish)
    }
    fun getWishes() : Flow<List<Wish>> {
        return wishDao.getAllWishes()
    }

    fun getAWishById(id: Long) : Flow<Wish> {
        return wishDao.getAWishById(id)
    }

    suspend fun updateAWish(wish: Wish) {
        wishDao.updateAWish(wish)
    }

    suspend fun deleteAWish(wish: Wish){
        wishDao.deleteAWish(wish)
    }
}