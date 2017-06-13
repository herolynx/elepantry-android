package com.herolynx.elepantry.ext.google.firebase.db

import com.herolynx.elepantry.core.repository.Repository
import com.herolynx.elepantry.core.rx.DataEvent
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.model.Tag
import org.funktionale.option.Option
import org.funktionale.option.firstOption
import rx.Observable

internal class TagRepository(
        private val resources: Repository<Resource>
) : Repository<Tag> {

    override fun findAll(): Observable<List<Tag>> = resources.findAll()
            .map { t ->
                t.map { t -> t.tags }
                        .reduceRight { set, acc -> set.plus(acc) }
            }

    override fun find(id: String): Observable<Option<Tag>> = findAll()
            .map { tags -> tags.filter { t -> t.id.equals(id) } }
            .map { tags -> tags.firstOption() }

    override fun asObservable(): Observable<DataEvent<Tag>> = Observable.error(UnsupportedOperationException("Tags stream not supported globally on tags"))

    override fun delete(t: Tag): Observable<DataEvent<Tag>> = Observable.error(UnsupportedOperationException("Delete not supported globally on tags"))

    override fun save(t: Tag): Observable<DataEvent<Tag>> = Observable.error(UnsupportedOperationException("Save not supported globally on tags"))

}