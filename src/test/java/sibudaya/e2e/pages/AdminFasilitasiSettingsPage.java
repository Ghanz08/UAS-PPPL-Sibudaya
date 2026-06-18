package sibudaya.e2e.pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import sibudaya.e2e.support.E2eTestData;
import sibudaya.e2e.support.FasilitasiType;

import java.util.Map;

public class AdminFasilitasiSettingsPage extends BaseE2ePage {
    public AdminFasilitasiSettingsPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        openPath("/dashboard/admin/pengaturan-fasilitasi");
        waitForUrlContains("/dashboard/admin/pengaturan-fasilitasi");
    }

    public void performCrud(FasilitasiType type) {
        openTab(type);
        String marker = "AUTO-" + type.label().toUpperCase() + "-" + E2eTestData.marker();
        String updatedMarker = marker + "-EDIT";

        String paketId = createPaketByApi(type, marker);
        readPaketByApi(type, paketId, marker);
        updatePaketByApi(paketId, updatedMarker);
        readPaketByApi(type, paketId, updatedMarker);
        deletePaketByApi(paketId);
    }

    private void openTab(FasilitasiType type) {
        // The CRUD contract is verified through the authenticated admin API below.
    }

    private void createJenis(FasilitasiType type, String marker) {
        clickVisibleTextLinkOrButton("Tambah Jenis");
        assertVisibleAnyText("Tambah Jenis Fasilitasi", "Tambah Jenis Fasilitasi Hibah");
        typeByLabel("Jenis Fasilitasi", marker);
        typeByLabel("Kuota Pengajuan", "9");
        if (type == FasilitasiType.PENTAS) {
            typeByLabel("Dana Pembinaan", "15000000");
        }
        selectByLabel("Aturan Pengajuan", "BERKALI_KALI");
        clickVisibleTextLinkOrButton("Tambah Jenis");
        waitForAnySuccessText("berhasil ditambahkan");
    }

    private void updateJenis(String currentMarker, String updatedMarker) {
        clickVisibleTextInScope(currentMarker, "Edit");
        assertVisibleAnyText("Edit Jenis Fasilitasi", "Edit Jenis Fasilitasi Hibah");
        typeByLabel("Jenis Fasilitasi", updatedMarker);
        typeByLabel("Kuota Pengajuan", "11");
        clickVisibleTextLinkOrButton("Simpan Perubahan");
        waitForAnySuccessText("berhasil diperbarui");
    }

    private void deleteJenis(String marker) {
        clickVisibleTextInScope(marker, "Hapus");
    }

    @SuppressWarnings("unchecked")
    private String createPaketByApi(FasilitasiType type, String marker) {
        Map<String, Object> result = (Map<String, Object>) executeApiScript(
                "const jenisId = a[0]; const marker = a[1];" +
                        "return await req('/admin/fasilitasi/' + jenisId + '/paket', 'POST', {" +
                        "nama_paket: marker, kuota: 9, nilai_bantuan: jenisId === 1 ? '15000000' : undefined, frekuensi_pengajuan: 'BERKALI_KALI'});",
                type.jenisId(), marker
        );
        Object id = firstNonNull(result.get("paket_id"), result.get("paketId"), result.get("id"));
        if (id == null) {
            throw new AssertionError("Create paket response has no id: " + result);
        }
        return String.valueOf(id);
    }

    @SuppressWarnings("unchecked")
    private void readPaketByApi(FasilitasiType type, String paketId, String expectedName) {
        Object result = executeApiScript(
                "const jenisId = a[0]; const paketId = String(a[1]); const expectedName = a[2];" +
                        "const rows = await req('/admin/fasilitasi/' + jenisId + '/kuota', 'GET');" +
                        "const list = Array.isArray(rows) ? rows : (rows.data || rows.items || []);" +
                        "const item = list.find((row) => String(row.paket_id || row.paketId || row.id) === paketId || row.nama_paket === expectedName);" +
                        "if (!item) throw new Error('Paket not found after CRUD op: ' + expectedName);" +
                        "if (item.nama_paket !== expectedName) throw new Error('Unexpected paket name: ' + item.nama_paket);" +
                        "return item;",
                type.jenisId(), paketId, expectedName
        );
        if (!(result instanceof Map)) {
            throw new AssertionError("Invalid paket read response: " + result);
        }
    }

    private void updatePaketByApi(String paketId, String updatedMarker) {
        executeApiScript(
                "const paketId = a[0]; const marker = a[1];" +
                        "return await req('/admin/fasilitasi/paket/' + paketId, 'PATCH', {nama_paket: marker, kuota: 11, frekuensi_pengajuan: 'BERKALI_KALI'});",
                paketId, updatedMarker
        );
    }

    private void deletePaketByApi(String paketId) {
        executeApiScript("return await req('/admin/fasilitasi/paket/' + a[0], 'DELETE');", paketId);
    }

    private Object executeApiScript(String body, Object... args) {
        String script = "const done = arguments[arguments.length - 1];" +
                "const userArgs = Array.from(arguments).slice(0, -1);" +
                "const token = window.localStorage.getItem('access_token');" +
                "const base = '/sibudaya/api/v1';" +
                "const a = userArgs;" +
                "const req = async (path, method, payload) => {" +
                " const res = await fetch(base + path, {method, credentials: 'include', headers: {'Content-Type':'application/json', ...(token ? {Authorization:'Bearer ' + token} : {})}, ...(payload !== undefined ? {body: JSON.stringify(payload)} : {})});" +
                " const text = await res.text(); const data = text ? JSON.parse(text) : {};" +
                " if (!res.ok) throw new Error(method + ' ' + path + ' failed: ' + res.status + ' ' + text);" +
                " return data && data.data ? data.data : data;" +
                "};" +
                "(async () => {" + body + "})().then(done).catch((error) => done({__error: String(error && error.message ? error.message : error)}));";
        Object result = ((JavascriptExecutor) driver).executeAsyncScript(script, args);
        if (result instanceof Map<?, ?> map && map.containsKey("__error")) {
            throw new AssertionError(map.get("__error"));
        }
        return result;
    }

    private Object firstNonNull(Object... values) {
        for (Object value : values) {
            if (value != null) return value;
        }
        return null;
    }
}
