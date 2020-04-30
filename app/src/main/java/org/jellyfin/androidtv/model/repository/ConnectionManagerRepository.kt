package org.jellyfin.androidtv.model.repository

import android.content.Context
import org.jellyfin.androidtv.BuildConfig
import org.jellyfin.androidtv.eventhandling.TvApiEventListener
import org.jellyfin.androidtv.model.compat.AndroidProfile
import org.jellyfin.androidtv.util.ProfileHelper
import org.jellyfin.apiclient.interaction.AndroidConnectionManager
import org.jellyfin.apiclient.interaction.AndroidDevice
import org.jellyfin.apiclient.interaction.VolleyHttpClient
import org.jellyfin.apiclient.interaction.connectionmanager.ConnectionManager
import org.jellyfin.apiclient.logging.AndroidLogger
import org.jellyfin.apiclient.model.logging.ILogger
import org.jellyfin.apiclient.model.serialization.GsonJsonSerializer
import org.jellyfin.apiclient.model.serialization.IJsonSerializer
import org.jellyfin.apiclient.model.session.ClientCapabilities
import org.jellyfin.apiclient.model.session.GeneralCommandType

class ConnectionManagerRepository private constructor(val connectionManager: ConnectionManager, val logger: ILogger, val serializer: IJsonSerializer) {
	companion object {
		@Volatile
		private var INSTANCE: ConnectionManagerRepository? = null

		fun getInstance(context: Context): ConnectionManagerRepository =
			INSTANCE ?: synchronized(this) {
				INSTANCE ?: buildConnectionManager(context).also { INSTANCE = it }
			}

		private fun buildConnectionManager(context: Context): ConnectionManagerRepository {
			val serializer: IJsonSerializer = GsonJsonSerializer()
			val logger: ILogger = AndroidLogger("Jellyfin-AndroidLogger")
			val capabilities = ClientCapabilities().apply {
				this.deviceProfile = AndroidProfile(ProfileHelper.getProfileOptions())
				this.playableMediaTypes = arrayListOf("Video", "Audio")
				this.supportsContentUploading = false
				this.supportsSync = false
				this.supportsMediaControl = true
				this.supportedCommands = arrayListOf(
					GeneralCommandType.DisplayContent.toString(),
					GeneralCommandType.DisplayMessage.toString()
				)
			}

			return ConnectionManagerRepository(
				AndroidConnectionManager(
					context.applicationContext,
					serializer,
					logger,
					VolleyHttpClient(logger, context.applicationContext),
					"Android TV",
					BuildConfig.VERSION_NAME,
					AndroidDevice(context.applicationContext),
					capabilities,
					TvApiEventListener()
				),
				logger,
				serializer
			)
		}
	}
}
