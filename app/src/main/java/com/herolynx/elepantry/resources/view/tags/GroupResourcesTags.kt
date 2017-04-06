package com.herolynx.elepantry.resources.view.tags

import com.herolynx.elepantry.core.repository.Repository
import com.herolynx.elepantry.core.rx.DataEvent
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.model.Tag
import org.funktionale.option.firstOption
import rx.Observable

internal class GroupResourcesTagsRepository(
        private val repository: Repository<Resource>,
        resources: List<Resource>
) : Repository<GroupResourcesTags> {

    private val ids = resources.map(Resource::id).toSet()

    override fun findAll() = repository.findAll()
            .map { l -> l.filter { e -> ids.contains(e.id) } }
            .map { l -> listOf(GroupResourcesTags(l)) }

    override fun find(id: String) = findAll().map { l -> l.firstOption() }

    override fun asObservable() = repository.findAll()
            .map { l -> DataEvent(GroupResourcesTags(l)) }

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
