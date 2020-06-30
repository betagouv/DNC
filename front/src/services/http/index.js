import superagent from 'superagent';

const agent = superagent.agent();

// Middleware to add api URL to calls with absolute URLs.
/* agent.use((req) => {
  if (req.url.startsWith('/')) {
    req.url = `https://randomuser.me${req.url}`;
  }

  return req;
}); */

export default agent;
