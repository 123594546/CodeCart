<template>
  <div class="app-page">
    <AppChrome>
      <router-view v-slot="{ Component }">
        <transition name="fade-slide" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </AppChrome>
  </div>
</template>

<script setup>
import AppChrome from '@/components/AppChrome.vue'
</script>
