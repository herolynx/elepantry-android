package com.herolynx.elepantry.resources.view.tags

import android.os.SystemClock
import com.herolynx.elepantry.core.repository.Repository
import com.herolynx.elepantry.core.rx.DataEvent
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.model.Tag
import rx.Observable

internal class GroupResourcesTagsRepository(
        private val repository: Repository<Resource>,
        resources: List<Resource>
) : Repository<GroupResourcesTags> {

    private val ids = resources.map(Resource::id).toSet()

    override fun findAll() = throw UnsupportedOperationException("not implemented")

    override fun find(id: String) = throw UnsupportedOperationException("not implemented")

    override fun asObservable() = Observable.defer {
        var resources: List<Resource> = listOf()
        var tries = 0
        do {
            resources = repository.findAll().getOrElse { listOf() }
            tries++
            SystemClock.sleep(50)
        } while (resources.isEmpty() && tries < 50)
        Observable.just(DataEvent(GroupResourcesTags(resources.filter { r -> ids.contains(r.id) })))
    }
//            repository.asObservable()
//            .filter { e -> !e.deleted && ids.contains(e.data.id) }
//            .map { e -> e.data }
//            .reduce(list(), { l, r -> l.plus(r) })
//            .map { l -> DataEvent(GroupResourcesTags(l)) }

    override fun delete(t: GroupResourcesTags): Observable<DataEvent<GroupResourcesTags>> {
        t.resources.forEach { r -> repository.delete(r) }
        return Observable.just(DataEvent(t))
    }

    override fun save(t: GroupResourcesTags): Observable<DataEvent<GroupResourcesTags>> {
        t.resources.forEach { r -> repository.save(r) }
        return Observable.just(DataEvent(t))
    }
}

internal class GroupResourcesTags(internal val resources: List<Resource>) {

    fun getTags(): List<Tag> = resources.flatMap(Resource::tags)
            .groupBy(Tag::name)
            .filterValues { v -> v.size > 1 }
            .flatMapTo(mutableListOf(), { v -> v.value })

    fun addTags(newTags: List<Tag>) = GroupResourcesTags(
            resources
                    .map { r ->
                        val allTags: Set<Tag> = mutableSetOf()
                        allTags.plus(r.tags)
                        allTags.plus(newTags)
                        r.copy(tags = allTags.toList())
                    }
    )

}
