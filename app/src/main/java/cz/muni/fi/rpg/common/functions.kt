package cz.muni.fi.rpg.common

import android.view.ViewGroup

typealias OnClickListener<TEntity> = (item: TEntity) -> Unit
typealias ViewHolderFactory<TEntity> = (parent: ViewGroup) -> ViewHolder<TEntity>