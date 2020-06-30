import Logger from 'utils/Logger';
import http from './http';

/**
 * @param encryptedUrl
 * @param accessToken
 */
async function verifJustificatif(encryptedUrl) {
  let justificatifData = [];

  try {
    justificatifData
      // .set({ Authorization: `Bearer ${accessToken}` })
      = (await http.get(`/api/justificatif${encryptedUrl}`)).body;
  } catch (error) {
    Logger.error(error);
  }

  return justificatifData;
}

export default {
  verifJustificatif,
};
