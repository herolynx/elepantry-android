package com.herolynx.elepantry.resources.view.tags

import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.error
import com.herolynx.elepantry.core.repository.Repository
import com.herolynx.elepantry.core.rx.DataEvent
import com.herolynx.elepantry.core.rx.schedule
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.model.Tag
import org.funktionale.option.firstOption
import rx.Observable
import rx.schedulers.Schedulers

internal class GroupResourcesTagsRepository(
        private val repository: Repository<Resource>,
        private val resources: List<Resource>
) : Repository<GroupResourcesTags> {

    private val ids = resources.map(Resource::id).toSet()

    override fun findAll() = repository.findAll()
            .map { l ->
                val inGroup = l.filter { e -> ids.contains(e.id) }
                val all: MutableSet<Resource> = mutableSetOf()
                all.addAll(inGroup)
                all.addAll(resources)
                all.toList()
            }
            .map { l -> listOf(GroupResourcesTags(l)) }

    override fun find(id: String) = findAll().map { l -> l.firstOption() }

    override fun asObservable() = repository.findAll()
            .map { l -> DataEvent(GroupResourcesTags(l)) }

    override fun delete(t: GroupResourcesTags): Observable<DataEvent<GroupResourcesTags>> {
        t.resources.forEach { r ->
            repository
                    .delete(r)
                    .schedule()
                    .observeOn(Schedulers.io())
                    .subscribe(
                            { r -> debug("[GroupResourcesTags] Resource deleted: $r") },
                            { ex -> error("[GroupResourcesTags] Couldn't delete group: $t") }
                    )
        }
        return Observable.just(DataEvent(t))
    }

    override fun save(t: GroupResourcesTags): Observable<DataEvent<GroupResourcesTags>> {
        t.resources.forEach { r ->
            repository
                    .save(r)
                    .schedule()
                    .observeOn(Schedulers.io())
                    .subscribe(
                            { r -> debug("[GroupResourcesTags] Resource changes saved: $r") },
                            { ex -> error("[GroupResourcesTags] Couldn't save changes - group: $t") }
                    )

        }
        return Observable.just(DataEvent(t))
    }
}

internal class GroupResourcesTags(internal val resources: List<Resource>) {

    fun getTags(): List<Tag> = resources.flatMap(Resource::tags)
            .groupBy(Tag::name)
            .flatMapTo(mutableListOf(), { v -> listOf(v.value[0]) })

    fun addTags(newTags: List<Tag>) = GroupResourcesTags(resources.map { r -> r.copy(tags = newTags.toList()) })

}
