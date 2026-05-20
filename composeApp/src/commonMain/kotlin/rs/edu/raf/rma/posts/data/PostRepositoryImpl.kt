package rs.edu.raf.rma.posts.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import rs.edu.raf.rma.core.db.AppDatabase
import rs.edu.raf.rma.networking.BeskarApi
import rs.edu.raf.rma.posts.domain.Post
import rs.edu.raf.rma.posts.domain.PostDetails
import rs.edu.raf.rma.posts.domain.PostRepository

class PostRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val beskarApi: BeskarApi,
) : PostRepository {

    override fun observePosts(mediaType: String): Flow<List<Post>> =
        appDatabase.postDao()
            .observePostsByMediaType(mediaType)
            .distinctUntilChanged()
            .map { rows -> rows.map { it.toDomain() } }

    override fun observePost(id: Int): Flow<PostDetails?> =
        appDatabase.postDao().observePostDetail(id)
            .map { row -> row?.toDomain() }

    override suspend fun refreshPosts() {
        val response = beskarApi.getPosts()
        val entities = response.items.map { it.toPostEntity() }
        appDatabase.postDao().refreshListTransaction(entities)
    }

    override suspend fun refreshPost(id: Int) {
        val model = beskarApi.getPost(id)
        val postEntity = model.toPostEntity()
        val detailsEntity = model.toPostDetailsEntity()
        val categoryEntities = model.categories.map { it.toCategoryEntity() }
        val categoryIds = model.categories.map { it.id }
        appDatabase.postDao().refreshDetailTransaction(
            post = postEntity,
            details = detailsEntity,
            categories = categoryEntities,
            categoryIds = categoryIds,
        )
    }
}
