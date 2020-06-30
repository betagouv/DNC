/**
 * String helper pour transformer les strings.
 * String.prototype est read-only.
 */

/**
 * Génère une chaine de caractère alphanumérique de taille length.
 *
 * @param {number} length - La taille de la chaine de caractère à générer.
 * @returns {undefined}
 */
function generateRandomString(length) {
  let result = '';
  const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  const charactersLength = characters.length;
  for (let i = 0; i < length; i++) {
    result += characters.charAt(Math.floor(Math.random() * charactersLength));
  }
  return result;
}

const stringUtils = {
  generateRandomString,
};

export default stringUtils;
