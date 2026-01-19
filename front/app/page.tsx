import ContactsTemplate from "@/components/templates/ContactsTemplate";
import { api } from "@/lib/api";

export default async function Page() {
  const data = await api.getContacts();

  return <ContactsTemplate initialContacts={data} />;
}
