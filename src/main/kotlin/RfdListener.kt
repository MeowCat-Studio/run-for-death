package org.meowcat.rfd

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.meowcat.rfd.data.Slot
import org.meowcat.rfd.events.CrazeEvent
import kotlin.coroutines.CoroutineContext

object RfdListener : Listener, CoroutineScope {
   override val coroutineContext: CoroutineContext
      get() = Dispatchers.Default

   @EventHandler
   suspend fun onPlayerKilledEvent(event: PlayerDeathEvent) {
      val victim = event.entity
      victim.uniqueId
      event.deathMessage = null
      val killer = victim.killer ?: run {
         broadcast("§6[赴死] §3${victim.name}擅自死了！")
         return
      }
      broadcast("§6[赴死] §3${killer.name}斩杀了${victim.name}!")

      slots.getOrPut(killer) { Slot(killer) }.update()
   }
   @EventHandler
   suspend fun onCrazeEvent(event: CrazeEvent) {
      val killer = event.player
      val location = killer.location
      broadcast(
         """§6[赴死] §c${event.player.name}陷入了疯狂！
         |>>>> 他在x${location.x.toInt()};y${location.y.toInt()};z${location.z.toInt()}""".trimMargin()
      )
      // 20 ticks/s
      val effect = PotionEffect(PotionEffectType.GLOWING, 60 * 20, 1)
      killer.addPotionEffect(effect)
      Vault.addMoney(killer, 200.0)
   }
}